package com.example.avatwin.Service


import com.example.avatwin.DataClass.chatGetBody
import com.example.avatwin.DataClass.chatUserGetBody
import retrofit2.Call
import retrofit2.http.*

interface ChatService {
    companion object{
        public val API_URL = " http://10.0.2.2:8080/api/v1/"
    }

    //사용자별 방 조회
    @GET("chat/user/room")
    fun get_userChat():Call<chatUserGetBody>


    //방 등록
    @POST("chat/room")
    fun post_chat(@Query("other") other: String):Call<chatGetBody>


}

