package com.android.chatbase

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.chatbase.adapter.MessageAdapter
import com.android.chatbase.model.Chat
import com.android.chatbase.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class MessageActivity : AppCompatActivity() {
    lateinit var profile_img_chat: CircleImageView
    lateinit var username_chat: TextView
    lateinit var firebaseUser: FirebaseUser
    lateinit var reference: DatabaseReference
    lateinit var btn_send: ImageButton
    lateinit var msg_send: EditText
    lateinit var messageAdapter: MessageAdapter
    lateinit var mChat: ArrayList<Chat>
    lateinit var msg_view: RecyclerView

    override fun onStart() {
        super.onStart()
        //        check if user is null
        if(FirebaseAuth.getInstance().currentUser!=null){
            firebaseUser = FirebaseAuth.getInstance().currentUser!!
        } else{
            startActivity(Intent(this@MessageActivity, StartActivity::class.java))
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_massage)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        profile_img_chat = findViewById(R.id.profile_img_chat)
        username_chat = findViewById(R.id.usernameMain_chat)
        btn_send = findViewById(R.id.btn_send)
        msg_send = findViewById(R.id.text_msg_send)
        msg_view = findViewById(R.id.msg_recycle_view)
        msg_view.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        msg_view.layoutManager = linearLayoutManager
        val userId = intent.getStringExtra("userId")
        btn_send.setOnClickListener {
            val msg = msg_send.text.toString()
            if (msg != "") {
                sendMessage(firebaseUser.uid, userId, msg)
            } else {
                Toast.makeText(
                    this@MessageActivity,
                    "Could not send empty message",
                    Toast.LENGTH_SHORT
                ).show()
            }
            msg_send.setText("")
        }
        if (userId != null) {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        } else {
            finish()
        }
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(
                    User::class.java
                )
                username_chat.text = user!!.username
                profile_img_chat.setImageResource(R.mipmap.ic_launcher)
                readMessage(firebaseUser.uid, userId)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendMessage(sender: String, receiver: String?, msg: String) {
        val referenceMsg = FirebaseDatabase.getInstance().reference
        val hashMap = HashMap<String, Any?>()
        hashMap["sender"] = sender
        hashMap["receiver"] = receiver
        hashMap["msg"] = msg
        referenceMsg.child("Chats").push().setValue(hashMap)
    }

    private fun readMessage(myId: String, userId: String?) {
        mChat = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mChat.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.receiver == myId && chat.sender == userId
                        || chat.sender == myId && chat.receiver == userId
                    ) {
                        mChat.add(chat)
                    }
                    messageAdapter = MessageAdapter(this@MessageActivity, mChat)
                    msg_view.adapter = messageAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun status(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        reference.updateChildren(hashMap) { _, _ -> }
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }
}