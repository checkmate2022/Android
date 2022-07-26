package com.example.avatwin.DataClass

import com.beust.klaxon.Json
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

//채팅방 생성
data class chatGetBody(val success:String,val data:chatBody)
data class chatBody(val id:String?, val name:String?, val username1:String?, val userImage1:String?,
                    val username2:String?, val userImage2:String?)

//채팅방 조회
data class chatUserGetBody(val list:ArrayList<chatBody>)

//채팅방 메시지 조회
data class chatMessageGetBody(val list:ArrayList<Chat>)

data class Chat (
        @Json(name = "type")
        val type: String,
        @Json(name = "sender")
        val sender: String,
        @Json(name = "message")
        val message: String
)

data class ChatRoom (
        val id: String,
        val name: String
)
