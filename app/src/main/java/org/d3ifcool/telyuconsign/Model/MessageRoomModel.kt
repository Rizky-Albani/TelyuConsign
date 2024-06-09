package org.d3ifcool.telyuconsign.Model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class MessageRoomModel(
    val text: String? = null,
    val chatroomId: String? = null,
    val username: String? = null,
    val message: String? = null,
    val imageUrl: String? = null,
    val time: String? = null
)