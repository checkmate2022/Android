package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface FcmService {
    companion object{
        public val API_URL = " http://10.0.2.2:8080/api/v1/"
    }

    //디바이스 토큰 등록
    @POST("fcm/register")
    fun post_fcmToken(@Query("deviceToken") deviceToken:String):Call<fcmResBody>

    //알람 목록 조회
    @GET("fcm")
    fun get_fcm():Call<fcmResBody>

    /*//알림보내기
    @POST("fcm")
    fun post_fcm(@Body requestDTO: fcmReqBody):Call<fcmResBody>*/
}

