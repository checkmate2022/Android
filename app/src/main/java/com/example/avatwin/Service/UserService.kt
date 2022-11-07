package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    companion object{
        public val API_URL = "http://172.30.1.17:8080/api/v1/"
    }

    //회원가입
    @POST("users/join")
    fun post_join(@Body join: joinReqBody):Call<joinGetBody>

    //유저정보조회
    @GET("users")
    fun get_user():Call<userGetBody>

    //유저검색
    @GET("users/search")
    fun get_user_search(@Query("query") query:String):Call<userGetBody2>

    //아이디 중복확인
    @POST("users/check/userId")
    fun check_userId(@Query("userId") userId:String):Call<userCheckBody>

    //닉네임 중복확인
    @POST("users/check/name")
    fun check_userName(@Query("username") username:String):Call<userCheckBody>

}

