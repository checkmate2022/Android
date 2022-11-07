package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface BoardService {
    companion object{
        public val API_URL = " http://172.30.1.17:8080/api/v1/"
    }


    //게시판 등록
    @POST("board")
    fun post_board(@Query("channelId") channelId:Long,
                     @Body boarddto: boardReqBody):Call<boardGetBody>

    //채널별 게시판 조회
    @GET("board/channel/{channelId}")
    fun get_channelBoard(@Path("channelId") channelId:Long):Call<boardTeamGetBody>

    //팀별 게시판 조회
    @GET("board")
    fun get_Board(@Query("teamId") teamId:Long):Call<boardTeamGetBody>

    //게시판 단건 조회
    @GET("board/{boardId}")
    fun get_BoardById(@Path("boardId") boardId:Long):Call<boardGetBodyById>

    //게시판 수정
    @PUT("board/{boardId}")
    fun put_board(@Path("boardId") boardId:Long,
                   @Body boarddto: boardReqBody):Call<boardGetBodyById>
    //게시판 삭제
    @DELETE("board/{boardId}")
    fun delete_board(@Path("boardId") boardId:Long):Call<ResponseBody>
}

