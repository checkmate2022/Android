package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import retrofit2.Call
import retrofit2.http.*

interface TeamService {
    companion object{
        public val API_URL = "http://10.0.2.2:8080/api/v1/"
    }

    //나의 팀 조회
    @GET("team")
    fun get_Team():Call<teamGetBody>
    @GET("team/user")
    fun get_myTeam():Call<myteamGetBody>

    //팀등록
    @POST("team")
    fun post_team(@Body teamdto:  teamReqBody):Call<teamPostGetBody>

    //아이디로 팀 조회
    @GET("team/{teamId}")
    fun get_teamId(@Path("teamId") teamId:Long):Call<teamIdGetBody>


    //아이디로 팀 참여자 조회
    @GET("team/{teamId}/user")
    fun get_teamUser(@Path("teamId") teamId:Long):Call<teamUserGetBody>

    //팀수정
    @PUT("team/{teamId}")
    fun put_team(@Path("teamId") teamId:Long,@Body teamdto: teamReqBody):Call<teamPostGetBody>

    //팀삭제
    @DELETE("team/{teamId}")
    fun delete_team(@Path("teamId") teamId:Long):Call<myteamGetBody>
}

