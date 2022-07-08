package com.example.avatwin.DataClass

import java.util.*
import kotlin.collections.ArrayList

//일정 생성
data class scheduleReqBody(val scheduleName:String?,val scheduleDescription:String?,
                           val scheduleStartDate: Date,val scheduleEndDate: Date,
                           val participantName: ArrayList<String>,val teamId: Long )

//팀아니디로 팀별 일정 조회
data class scheduleGetBody(val list:ArrayList<scheduleReqBody>)


