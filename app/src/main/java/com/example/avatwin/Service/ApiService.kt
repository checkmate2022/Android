package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    companion object{
        //172.20.31.40:8080
        public val API_URL = "http://172.20.31.3:8080/api/v1/"
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

