package com.android.chatbase.usersfragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.chatbase.MessageActivity
import com.android.chatbase.R
import com.android.chatbase.adapter.ChatFragmentAdapter
import com.android.chatbase.adapter.ClickUser
import com.android.chatbase.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class UsersFragment : Fragment() , ClickUser {
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatFragmentAdapter: ChatFragmentAdapter
    private lateinit var mUsers: ArrayList<User>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_users, container, false)
        recyclerView = view.findViewById(R.id.user_recycle_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        mUsers = ArrayList()
        chatFragmentAdapter = ChatFragmentAdapter(this)
        recyclerView.adapter = chatFragmentAdapter
        readUser()
        return view
    }

    private fun readUser() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("TAG", "onDataChange: "+mUsers.size)
                mUsers.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(
                        User::class.java
                    )
                    Log.d("TAG", "onDataChange: "+user.toString())

                    if (user!!.id == firebaseUser!!.uid) continue
                    mUsers.add(user)
                    Log.d("TAG", "onDataChange: "+mUsers.size)
                }
                chatFragmentAdapter.submitList(mUsers)
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