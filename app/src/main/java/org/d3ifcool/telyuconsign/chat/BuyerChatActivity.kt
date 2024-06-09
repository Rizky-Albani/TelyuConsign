package org.d3ifcool.telyuconsign.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import org.d3ifcool.telyuconsign.Adapter.BuyerChatAdapter
import org.d3ifcool.telyuconsign.Adapter.SellerChatAdapter
import org.d3ifcool.telyuconsign.Model.ChatRoomModel
import org.d3ifcool.telyuconsign.R
import org.d3ifcool.telyuconsign.databinding.ActivityBuyerChatBinding
import org.d3ifcool.telyuconsign.ui.customView.ListBarangActivity

class BuyerChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBuyerChatBinding
    private lateinit var myAdapter: BuyerChatAdapter
    var chatRooms = ArrayList<ChatRoomModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        val auth = Firebase.auth
        val user = auth.currentUser
        val uid = user?.uid
        getChatRoom(uid.toString())
        binding.startchat.setOnClickListener {
            val intent = Intent(this, ListBarangActivity::class.java)
            intent.putExtra("category", "chat")
            startActivity(intent)
        }

    }

    fun getChatRoom(uid: String): ArrayList<ChatRoomModel> {
        val db = FirebaseFirestore.getInstance()
        val chatRoomRef = db.collection("chatRoom").whereEqualTo("userIdBuyer", uid)
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
                myAdapter = BuyerChatAdapter(chatRooms)
                with(binding.recylerView) {
                    addItemDecoration(
                        DividerItemDecoration(context,
                            RecyclerView.VERTICAL)
                    )
                    adapter = myAdapter
                    setHasFixedSize(true)
                }
            }
        }
        return chatRooms
    }

    override fun onPause() {
        super.onPause()
        chatRooms.clear()
    }


}