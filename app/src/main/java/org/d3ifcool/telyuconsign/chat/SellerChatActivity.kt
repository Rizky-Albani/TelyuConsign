package org.d3ifcool.telyuconsign.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import org.d3ifcool.telyuconsign.Adapter.ChatRecyclerAdapter
import org.d3ifcool.telyuconsign.Adapter.SellerChatAdapter
import org.d3ifcool.telyuconsign.Model.ChatRoomModel
import org.d3ifcool.telyuconsign.R
import org.d3ifcool.telyuconsign.databinding.ActivitySellerChatBinding
import org.d3ifcool.telyuconsign.ui.AddActivity

class SellerChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySellerChatBinding
    private lateinit var myAdapter: SellerChatAdapter
    var chatRooms = ArrayList<ChatRoomModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        val auth = Firebase.auth
        val user = auth.currentUser
        val uid = user?.uid
        getChatRoom(uid.toString())
        binding.startchat.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }

    }

    fun getChatRoom(uid: String): ArrayList<ChatRoomModel> {
        val db = FirebaseFirestore.getInstance()
        val chatRoomRef = db.collection("chatRoom").whereEqualTo("userIdSeller", uid)
        chatRoomRef.get().addOnSuccessListener {
            if (it.isEmpty) {
                binding.noData.visibility = View.VISIBLE
                binding.startchat.visibility = View.VISIBLE
            } else {
                binding.noData.visibility = View.GONE
                binding.startchat.visibility = View.GONE
            }
            for (document in it) {
                val chatRoom = ChatRoomModel(
                    document.id,
                    document.data["userIdBuyer"].toString(),
                    document.data["userIdSeller"].toString(),
                    document.data["usernameBuyer"].toString(),
                    document.data["usernameSeller"].toString(),
                    document.data["userImageBuyer"].toString(),
                    document.data["userImageSeller"].toString(),
                    document.data["lastMessage"].toString(),
                    document.data["lastMessageTimestamp"].toString(),
                )
                chatRooms.add(chatRoom)
                myAdapter = SellerChatAdapter(chatRooms)
                with(binding.recylerView) {
                    addItemDecoration(
                        DividerItemDecoration(context,
                            RecyclerView.VERTICAL)
                    )
                    adapter = myAdapter
                    setHasFixedSize(true)
                }
            }
        }.addOnFailureListener() {
            Log.d("TAG", "Failed to get chat room")
        }
        return chatRooms
    }

    override fun onPause() {
        super.onPause()
        chatRooms.clear()
    }

}