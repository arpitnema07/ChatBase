package com.android.chatbase.chatfragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.chatbase.MessageActivity
import com.android.chatbase.R
import com.android.chatbase.adapter.ChatFragmentAdapter
import com.android.chatbase.adapter.ClickUser
import com.android.chatbase.model.Chat
import com.android.chatbase.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*

class ChatFragment : Fragment(), ClickUser {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: ChatFragmentAdapter
    private lateinit var mUser: ArrayList<User>
    lateinit var firebaseUser: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var userList: ArrayList<String>

    override fun onStart() {
        super.onStart()
        // check if user is null
        if(FirebaseAuth.getInstance().currentUser!=null){
            firebaseUser = FirebaseAuth.getInstance().currentUser!!
        } else{
            Toast.makeText(requireContext(),"Error Occurred",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView = view.findViewById(R.id.chats_recycle_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        userList = ArrayList()
        mUser = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("Chats")


        userAdapter = ChatFragmentAdapter(this)
        recyclerView.adapter = userAdapter
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.sender == firebaseUser.uid) {
                        userList.add(chat.receiver)
                    }
                    if (chat.receiver == firebaseUser.uid) {
                        userList.add(chat.sender)
                    }
                }
                readChats()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return view
    }

    private fun readChats() {
        val ids: MutableSet<String> = HashSet()
        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUser.clear()
                ids.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(
                        User::class.java
                    )
                    for (id in userList) {
                        if (user!!.id == id && !ids.contains(user.id)) {
                            mUser.add(user)
                            ids.add(user.id)
                        }
                    }
                }
                userAdapter.submitList(mUser)

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onItemClicked(user: User) {
        val intent = Intent(this.context, MessageActivity::class.java)
        intent.putExtra("userId", user.id)
        startActivity(intent)
    }
}