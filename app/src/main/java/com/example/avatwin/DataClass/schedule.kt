package com.example.avatwin.DataClass

import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

//일정 생성
data class scheduleReqBody(val scheduleName:String?,val scheduleDescription:String?,val scheduleType:String?,
                           val scheduleStartdate: String,val scheduleEnddate: String,
                           val participantName: ArrayList<String>,val teamId: Long )
//팀아니디로 팀별 일정 조회
data class scheduleGetBody(val success:String?,val code:Long?, val msg:String?,val data:scheduleReqBody)

//팀아니디로 팀별 일정 조회
data class scheduleTeamGetBody(val list:ArrayList<scheduleReqBody>)


