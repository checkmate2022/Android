package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface CommentService {
    companion object{
        public val API_URL = " http://10.0.2.2:8080/api/v1/"
    }


    //댓글 등록
    @POST("comment")
    fun post_comment(@Query("boardId") boardId:Long,
                     @Query("content") content: String):Call<commentGetBody>


}

