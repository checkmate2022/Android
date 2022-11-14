package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ScheduleService {
    companion object{
        public val API_URL = " http://172.30.1.17:8080/api/v1/"
    }

    //일정등록
    @POST("schedule")
    fun post_schedule(@Body scheduledto: scheduleReqBody):Call<scheduleGetBody>

    //팀별 일정 가져오기
    @GET("schedule/team/{teamId}")
    fun get_teamSchedule(@Path("teamId") teamId:Long):Call<scheduleTeamGetBody>

    //챗봇 알람 설정할 수 있는 일정 가져오기
    @GET("chatbot")
    fun get_chatbotSchedule():Call<scheduleTeamGetBody>

    //단건 일정 조회
    @GET("schedule/{scheduleId}")
    fun get_ScheduleById(@Path("scheduleId") scheduleId:Long):Call<scheduleGetBody>

    //일정 수정
    @PUT("schedule/{scheduleId}")
    fun put_schedule(@Path("scheduleId") scheduleId:Long,
                  @Body scheduledto: scheduleReqBody):Call<scheduleGetBody>
    //일정 삭제
    @DELETE("schedule/{scheduleId}")
    fun delete_schedule(@Path("scheduleId") scheduleId:Long):Call<ResponseBody>

}

