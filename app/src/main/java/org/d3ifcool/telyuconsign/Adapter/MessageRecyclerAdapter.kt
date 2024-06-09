package org.d3ifcool.telyuconsign.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import org.d3ifcool.telyuconsign.Model.MessageRoomModel
import org.d3ifcool.telyuconsign.R
import org.d3ifcool.telyuconsign.databinding.ChatMessageRecyclerBinding
import org.d3ifcool.telyuconsign.databinding.ImageMessageBinding

class MessageRecyclerAdapter(
    private val options: FirebaseRecyclerOptions<MessageRoomModel>,
    private val currentUserName: String?
) : FirebaseRecyclerAdapter<MessageRoomModel, RecyclerView.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_TEXT) {
            val view = inflater.inflate(R.layout.chat_message_recycler, parent, false)
            val binding = ChatMessageRecyclerBinding.bind(view)
            MessageViewHolder(binding)
        } else {
            val view = inflater.inflate(R.layout.image_message, parent, false)
            val binding = ImageMessageBinding.bind(view)
            ImageMessageViewHolder(binding)
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: MessageRoomModel) {
        if (options.snapshots[position].text != null) {
            (holder as MessageViewHolder).bind(model)
        } else {
            (holder as ImageMessageViewHolder).bind(model)
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if (options.snapshots[position].text != null) VIEW_TYPE_TEXT else VIEW_TYPE_IMAGE
    }

    inner class MessageViewHolder(
        private val binding: ChatMessageRecyclerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: MessageRoomModel) = with(binding) {
            leftChatTextview.text = chat.message
            rightChatTextview.text = chat.message
            leftChatTimeview.text = chat.time
            rightChatTimeview.text = chat.time
            leftChatLayout.visibility = if (chat.username == currentUserName) {
                android.view.View.GONE
            } else {
                android.view.View.VISIBLE
            }
            rightChatLayout.visibility = if (chat.username == currentUserName) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
            leftChatTimeview.visibility = if (chat.username == currentUserName) {
                android.view.View.GONE
            } else {
                android.view.View.VISIBLE
            }
            rightChatTimeview.visibility = if (chat.username == currentUserName) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
            root.setOnClickListener {
                val message = root.context.getString(R.string.message, chat.username)
                Toast.makeText(root.context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    inner class ImageMessageViewHolder(private val binding: ImageMessageBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: MessageRoomModel) {
            loadImageIntoView(binding.messageImageViewLeft, item.imageUrl!!, false)
            loadImageIntoView(binding.messageImageViewRight, item.imageUrl!!, false)
            binding.messageImageViewLeft.visibility = if (item.username == currentUserName) {
                android.view.View.GONE
            } else {
                android.view.View.VISIBLE
            }
            binding.messageImageViewRight.visibility = if (item.username == currentUserName) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
        }

        private fun loadImageIntoView(view: ImageView, url: String, isCircular: Boolean = true) {
            if (url.startsWith("gs://")) {
                val storageReference = Firebase.storage.getReferenceFromUrl(url)
                storageReference.downloadUrl
                    .addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        loadWithGlide(view, downloadUrl, isCircular)
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            TAG,
                            "Getting download url was not successful.",
                            e
                        )
                    }
            } else {
                loadWithGlide(view, url, isCircular)
            }
        }
    }

    private fun loadWithGlide(view: ImageView, url: String, isCircular: Boolean = true) {
        Glide.with(view.context).load(url).into(view)
        var requestBuilder = Glide.with(view.context).load(url)
        if (isCircular) {
            requestBuilder = requestBuilder.transform(CircleCrop())
        }
        requestBuilder.into(view)
    }

    companion object {
        const val TAG = "MessageAdapter"
        const val VIEW_TYPE_TEXT = 1
        const val VIEW_TYPE_IMAGE = 2
    }

}