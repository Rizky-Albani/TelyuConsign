package org.d3ifcool.telyuconsign.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.d3ifcool.telyuconsign.ui.ChatActivity
import org.d3ifcool.telyuconsign.Model.ChatRoomModel
import org.d3ifcool.telyuconsign.databinding.ChatRecyclerBinding

class ChatRecyclerAdapter(private val data:List<ChatRoomModel>) : RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder>() {
    class ViewHolder(
        private val binding: ChatRecyclerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatRoomModel) = with(binding) {
            userNameText.text = "iky"
            lastMessageText.text = chat.lastMessage
            lastMessageTimeText.text = chat.lastMessageTimestamp


            root.setOnClickListener {
                val username = "iky"
                var intent = android.content.Intent(root.context, ChatActivity::class.java)
                intent.putExtra("username", username)
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