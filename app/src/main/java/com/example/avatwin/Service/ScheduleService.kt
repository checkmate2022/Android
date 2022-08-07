package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import okhttp3.ResponseBody
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
    fun get_teamSchedule(@Path("teamId") teamId:Long):Call<scheduleTeamGetBody>

    //일정 수정
    @PUT("schedule/{scheduleId}")
    fun put_schedule(@Path("scheduleId") scheduleId:Long,
                  @Body scheduledto: scheduleReqBody):Call<scheduleGetBody>
    //일정 삭제
    @DELETE("schedule/{scheduleId}")
    fun delete_schedule(@Path("scheduleId") scheduleId:Long):Call<ResponseBody>

}

