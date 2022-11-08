package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ChatbotService {
    companion object{
        public val API_URL = " http://172.20.4.93:8080/"
    }


    //챗봇 알람 설정할 수 있는 일정 가져오기
    @GET("chatbot")
    fun get_chatbotSchedule():Call<scheduleTeamGetBody>



}

