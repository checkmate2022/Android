package com.example.avatwin.Service


import com.example.avatwin.DataClass.chatGetBody
import com.example.avatwin.DataClass.chatMessageGetBody
import com.example.avatwin.DataClass.chatUserGetBody
import retrofit2.Call
import retrofit2.http.*

interface ChatService {
    companion object{
        public val API_URL = " http://172.20.7.122:8080/api/v1/"
    }

    //사용자별 방 조회
    @GET("chat/user/room")
    fun get_userChat():Call<chatUserGetBody>


    //방 등록
    @POST("chat/room")
    fun post_chat(@Query("other") other: String):Call<chatGetBody>

  //방별메시지보기
  @GET("chat/room/{roomId}")
  fun get_chatMessage(@Path("roomId") roomId:String):Call<chatMessageGetBody>
}

