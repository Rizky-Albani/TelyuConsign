package org.d3ifcool.telyuconsign.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.d3ifcool.telyuconsign.Model.ChatRoomModel

class ChatViewModel : ViewModel() {
    var db = FirebaseFirestore.getInstance()
    var chatRoomRef = db.collection("chatRoom")
    var chatRoomList = MutableLiveData<ArrayList<ChatRoomModel>?>()


}
