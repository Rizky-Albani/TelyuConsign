package org.d3ifcool.telyuconsign.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import org.d3ifcool.telyuconsign.Model.ChatRoomModel
import org.d3ifcool.telyuconsign.databinding.ChatRecyclerBinding
import org.d3ifcool.telyuconsign.ui.ChatActivity

class SellerChatAdapter(private val data:List<ChatRoomModel>) : RecyclerView.Adapter<SellerChatAdapter.ViewHolder>() {
    class ViewHolder(
        private val binding: ChatRecyclerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var database: DatabaseReference

        fun bind(chat: ChatRoomModel) = with(binding) {
            userNameText.text = chat.usernameBuyer
            database = Firebase.database.reference
            val messageRef = database.child("messageRoom").child(chat.chatroomId).limitToLast(1)
            messageRef.get().addOnSuccessListener {
                if (it.exists()) {
                    val lastMessage = it.children.elementAt(0).child("message").value.toString()
                    val lastMessageTimestamp = it.children.elementAt(0).child("time").value.toString()
                    if (it.children.elementAt(0).child("username").value.toString() != chat.usernameBuyer) {
                        lastMessageText.text = "You: $lastMessage"
                    } else {
                        lastMessageText.text = lastMessage
                    }


                    lastMessageTimeText.text = lastMessageTimestamp
                } else {
                    lastMessageText.text = "Chat now..."
                }
            }
            Glide.with(root.context)
                .load(chat.userImageBuyer)
                .circleCrop()
                .into(profilePicImageView)

            root.setOnClickListener {
                val chatroomId = chat.chatroomId
                var intent = android.content.Intent(root.context, ChatActivity::class.java)
                intent.putExtra("chatroomId", chatroomId)
                root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ChatRecyclerBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}