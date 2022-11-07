package com.example.avatwin.DataClass

import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

//일정 생성
data class scheduleReqBody(val scheduleName:String?,val scheduleDescription:String?,val scheduleType:String?,
                           val scheduleStartDate: LocalDateTime,val scheduleEndDate: LocalDateTime,
                           val participantName: ArrayList<String>,val teamId: Long,val notificationTime: Int )

//일정 생성
data class scheduleBody(val scheduleSeq:Long?,val scheduleName:String?,val scheduleDescription:String?,val scheduleType:String?,
                           val scheduleStartDate: LocalDateTime,val scheduleEndDate: LocalDateTime,val participants: ArrayList<String>,val teamId: Long,val notificationTime:Int )
//팀아니디로 팀별 일정 조회
data class scheduleGetBody(val success:String?,val code:Long?, val msg:String?,val data:scheduleBody)

//팀아니디로 팀별 일정 조회
data class scheduleTeamGetBody(val success: String?, val list:ArrayList<scheduleBody>)


