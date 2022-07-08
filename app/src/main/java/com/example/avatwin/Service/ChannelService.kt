package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import retrofit2.Call
import retrofit2.http.*

interface ChannelService {
    companion object{
        public val API_URL = " http://10.0.2.2:8080/api/v1/"
    }


    //채널등록
    @POST("channel")
    fun post_channel(@Query("teamId") teamId:Long,
                     @Query("channelName") channelName:String):Call<channelGetBody>

    //팀별 채널 조회
    @GET("channel/team/{teamId}")
    fun get_teamChannel(@Path("teamId") teamId:Long):Call<channelTeamGetBody>



}

