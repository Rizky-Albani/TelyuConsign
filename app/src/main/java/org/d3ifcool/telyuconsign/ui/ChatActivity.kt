package org.d3ifcool.telyuconsign.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import org.d3ifcool.telyuconsign.Adapter.MessageRecyclerAdapter
import org.d3ifcool.telyuconsign.Model.ChatRoomModel
import org.d3ifcool.telyuconsign.Model.MessageRoomModel
import org.d3ifcool.telyuconsign.databinding.ActivityChatBinding


class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var myAdapter: MessageRecyclerAdapter
    private lateinit var database: DatabaseReference
    private val openDocument = registerForActivityResult(MyOpenDocumentContract()) { uri ->
        uri?.let { onImageSelected(it) }
    }

    private lateinit var chatroomId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = Firebase.database.reference
        val intent = intent
        val auth = Firebase.auth
        val user = auth.currentUser
        val uid = user?.uid
        val username = user?.displayName
        chatroomId = intent.getStringExtra("chatroomId").toString()
        val db = FirebaseFirestore.getInstance()
        val chatRoomRef = db.collection("chatRoom").document(chatroomId)
        chatRoomRef.get().addOnSuccessListener {
            val chatRoom = ChatRoomModel(
                it.id,
                it.data?.get("userIdBuyer").toString(),
                it.data?.get("userIdSeller").toString(),
                it.data?.get("usernameBuyer").toString(),
                it.data?.get("usernameSeller").toString(),
                it.data?.get("userImageBuyer").toString(),
                it.data?.get("userImageSeller").toString(),
                it.data?.get("lastMessage").toString(),
                it.data?.get("lastMessageTimestamp").toString(),
            )
            if (chatRoom.userIdBuyer == uid) {
                binding.otherUsername.text = chatRoom.usernameSeller
                Glide.with(this)
                    .load(chatRoom.userImageSeller)
                    .circleCrop()
                    .into(binding.profilePicImageView)
            } else {
                binding.otherUsername.text = chatRoom.usernameBuyer
                Glide.with(this)
                    .load(chatRoom.userImageBuyer)
                    .circleCrop()
                    .into(binding.profilePicImageView)
            }
        }


        binding.messageSendBtn.setOnClickListener {
            val message = binding.chatMessageInput.text.toString()
            if (message.isEmpty()) return@setOnClickListener
            val calendar = java.util.Calendar.getInstance()
            val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val minute = calendar.get(java.util.Calendar.MINUTE)
            var time = ""
            if(hour < 10 && minute < 10){
                time = "0$hour.0$minute"
            } else if(hour < 10){
                time = "0$hour.$minute"
            } else if(minute < 10){
                time = "$hour.0$minute"
            } else {
                time = "$hour.$minute"
            }
            val messageRoom = MessageRoomModel(
                text = message,
                chatroomId = chatroomId,
                username = username,
                message = message,
                imageUrl = null,
                time = time
            )
            database.child("messageRoom").child(chatroomId.toString()).push().setValue(messageRoom)
            binding.chatMessageInput.text.clear()
        }

        binding.chatMessageInput.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.messageSendBtn.performClick()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        val messageRef = database.child("messageRoom").child(chatroomId)

        val options = FirebaseRecyclerOptions.Builder<MessageRoomModel>()
            .setQuery(messageRef, MessageRoomModel::class.java)
            .build()
        myAdapter = MessageRecyclerAdapter(options, username.toString())

        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        binding.chatRecyclerView.layoutManager = manager
        binding.chatRecyclerView.adapter = myAdapter


        myAdapter.registerAdapterDataObserver(
            MyScrollToBottomObserver(binding.chatRecyclerView, myAdapter, manager)
        )

        binding.addMessageImageView.setOnClickListener {
            openDocument.launch(arrayOf("image/*"))
        }
    }

    private fun onImageSelected(uri: Uri) {
        Log.d("TAG", "Uri: $uri")
        val auth = Firebase.auth
        val user = auth.currentUser
        val username = user?.displayName
        val calendar = java.util.Calendar.getInstance()
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = calendar.get(java.util.Calendar.MINUTE)
        var time = ""
        if(hour < 10 && minute < 10){
            time = "0$hour.0$minute"
        } else if(hour < 10){
            time = "0$hour.$minute"
        } else if(minute < 10){
            time = "$hour.0$minute"
        } else {
            time = "$hour.$minute"
        }
        val tempMessage = MessageRoomModel(null, chatroomId, username, "$username send a photo", "https://www.google.com/images/spin-32.gif", time)
        database.child("messageRoom").child(chatroomId.toString()).push()
            .setValue(tempMessage) { databaseError, databaseReference ->
                if (databaseError == null) {
                    val key = databaseReference.key
                    val storageReference = Firebase.storage
                        .getReference("message")
                        .child(key!!)
                        .child(uri.lastPathSegment!!)
                    putImageInStorage(storageReference, uri, key)
                } else {
                    Log.w(
                        "TAG",
                        "Unable to write message to database.",
                        databaseError.toException()
                    )
                }
            }
    }

    private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String?) {
        // First upload the image to Cloud Storage
        val calendar = java.util.Calendar.getInstance()
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = calendar.get(java.util.Calendar.MINUTE)
        var time = ""
        if(hour < 10 && minute < 10){
            time = "0$hour.0$minute"
        } else if(hour < 10){
            time = "0$hour.$minute"
        } else if(minute < 10){
            time = "$hour.0$minute"
        } else {
            time = "$hour.$minute"
        }
        val auth = Firebase.auth
        val user = auth.currentUser
        val username = user?.displayName
        storageReference.putFile(uri)
            .addOnSuccessListener(
                this
            ) { taskSnapshot -> // After the image loads, get a public downloadUrl for the image
                // and add it to the message.
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        val friendlyMessage = MessageRoomModel(
                            null,
                            chatroomId,
                            username.toString(),
                            "$username send a photo",
                            uri.toString(),
                            time
                        )
                        database.child("messageRoom").child(chatroomId.toString()).child(key!!)
                            .setValue(friendlyMessage)
                    }
            }
            .addOnFailureListener(this) { e ->
                Log.w(
                    "TAG",
                    "Image upload task was unsuccessful.",
                    e
                )
            }
    }

    public override fun onPause() {
        myAdapter.stopListening()
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        myAdapter.startListening()
    }


/*    private fun getData1(): List<MessageRoomModel> {
        return listOf(
            MessageRoomModel("user", "ready?", "21.21"),
            MessageRoomModel("", "ready bang", "21.22"),
            MessageRoomModel("user", "nego dikit bang...", "21.25"),
            )
    }
    private fun getData2(): List<MessageRoomModel> {
        return listOf(
            MessageRoomModel("user", "ready?", "21.21"),
            MessageRoomModel("", "ready bang", "21.22"),
            MessageRoomModel("user", "Kondisi barang gimana", "21.25"),
            MessageRoomModel("", "Barang bagus bang, beda dikit dengan yang di foto", "21.29"),
            MessageRoomModel("user", "Beda dikit ga ngaruh...", "21.50"),
        )
    }
    private fun getData3(): List<MessageRoomModel> {
        return listOf(
            MessageRoomModel("user", "ready?", "21.21"),
            MessageRoomModel("", "ready bang", "21.22"),
            MessageRoomModel("user", "lokasi mana?", "21.25"),
            MessageRoomModel("", "di daerah sukabirus", "21.29"),
            MessageRoomModel("user", "Deal", "10.10"),
        )
    }*/
}