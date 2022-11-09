package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ChannelService {
    companion object{
        public val API_URL = " http://172.20.7.122:8080/api/v1/"
    }


    //채널등록
    @POST("channel")
    fun post_channel(@Query("teamId") teamId:Long,
                     @Query("channelName") channelName:String):Call<channelGetBody>

    //팀별 채널 조회
    @GET("channel/team/{teamId}")
    fun get_teamChannel(@Path("teamId") teamId:Long):Call<channelTeamGetBody>

    //채널 수정
    @PUT("channel/{channelId}")
    fun put_channel(@Path("channelId") channelId:Long,
                  @Query("channelName") channelName: String):Call<channelGetBody>
    //채널 삭제
    @DELETE("channel/{channelId}")
    fun delete_channel(@Path("channelId") channelId:Long):Call<channelRes>

}

