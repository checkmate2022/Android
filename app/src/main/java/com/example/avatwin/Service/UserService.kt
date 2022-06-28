package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    companion object{
        public val API_URL = " http://10.0.2.2:8080/api/v1/"
    }

    //회원가입
    @POST("users/join")
    fun post_join(@Body join: joinReqBody):Call<joinGetBody>

    //유저정보조회
    @GET("users")
    fun get_user():Call<userGetBody>

}

