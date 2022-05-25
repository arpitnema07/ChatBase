package com.android.chatbase.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.chatbase.R
import com.android.chatbase.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*

class MessageAdapter(private val mContext: Context, private val mChat: ArrayList<Chat>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    lateinit var firebaseUser: FirebaseUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = if (viewType == MSG_TYPE_RIGHT) {
            LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false)
        } else {
            LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = mChat[position]
        holder.show_msg.text = chat.msg
    }

    override fun getItemCount(): Int {
        return mChat.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var show_msg: TextView = itemView.findViewById(R.id.show_msg)

    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        return if (mChat[position].sender == firebaseUser.getUid()) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

    companion object {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1
    }
}