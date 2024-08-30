package com.example.chatwithfirebase.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Chats(
    val idUserRemetente : String  ="",
    val idUserDestinatario : String  ="",
    val photo : String  ="",
    val name : String  ="",
    val lastMessage: String = "",
    @ServerTimestamp
    val date : Date?  =null,
)
