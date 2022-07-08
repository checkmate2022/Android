package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import retrofit2.Call
import retrofit2.http.*

interface ScheduleService {
    companion object{
        public val API_URL = " http://10.0.2.2:8080/api/v1/"
    }

    //일정등록
    @POST("schedule")
    fun post_schedule(@Body scheduledto: scheduleReqBody):Call<scheduleGetBody>

    //팀별 일정 가져오기
    @GET("schedule/team/{teamId}")
    fun get_teamSchedule(@Path("teamId") teamId:Long):Call<scheduleGetBody>



}

