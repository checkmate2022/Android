package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    companion object{
        //192.168.0.14 10.0.2.2:8080 172.20.7.122
        public val API_URL = "http://192.168.0.14:8080/api/v1/"
    }
    //auth
    @POST("auth/login")
    fun post_login(@Body login: loginReqBody):Call<loginGetBody>


    //team
    @GET("team")
    fun get_Team():Call<teamGetBody>
    @GET("team/user")
    fun get_myTeam():Call<myteamGetBody>
}

