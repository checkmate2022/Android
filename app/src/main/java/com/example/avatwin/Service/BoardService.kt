package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import retrofit2.Call
import retrofit2.http.*

interface BoardService {
    companion object{
        public val API_URL = " http://10.0.2.2:8080/api/v1/"
    }


    //게시판 등록
    @POST("board")
    fun post_board(@Query("channelId") channelId:Long,
                     @Body boarddto: boardReqBody):Call<boardGetBody>

    //채널별 게시판 조회
    @GET("channel/team/{teamId}")
    fun get_channelBoard(@Path("channelId") channelId:Long):Call<boardTeamGetBody>

    //팀별 게시판 조회
    @GET("board")
    fun get_Board(@Query("teamId") teamId:Long):Call<boardTeamGetBody>


}

