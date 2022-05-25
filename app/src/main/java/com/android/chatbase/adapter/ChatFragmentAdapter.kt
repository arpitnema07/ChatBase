package com.android.chatbase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.chatbase.R
import com.android.chatbase.model.User
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class ChatFragmentAdapter(private val listener: ClickUser) : ListAdapter<User,ChatFragmentAdapter.ViewHolder>(UserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user,listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView = itemView.findViewById(R.id.listUserName)
        var profile_image: CircleImageView = itemView.findViewById(R.id.profile_img_list)
        fun bind(user: User, listener: ClickUser,) {
            val colorOn = ContextCompat.getColor(itemView.context, R.color.online)
            val colorOff = ContextCompat.getColor(itemView.context, R.color.offline)
            username.text = user.username
            Glide.with(itemView.context).load(R.drawable.profile).into(profile_image)
            if (user.status == "online") {
                profile_image.borderColor = colorOn
            } else {
                profile_image.borderColor = colorOff
            }
            itemView.setOnClickListener {
                listener.onItemClicked(user)
            }
        }
    }
}



interface ClickUser{
    fun onItemClicked(user: User)
}
class UserDiffCallback: DiffUtil.ItemCallback<User>(){
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }

}