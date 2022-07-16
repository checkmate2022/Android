package com.example.avatwin.DataClass

import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

//게시판 생성
data class boardReqBody(val title:String?,val content:String?)
data class boardGetBody(val data:boardBody)
data class boardBody(val boardSeq:Long?, val title:String?,val content:String?, val user:userData)

//팀별 게시판조회 = 전체게시판
data class boardTeamGetBody(val list:ArrayList<boardTeamBody>)
data class boardTeamBody(val boardSeq:Long?, val title:String?, val content:String?, val createDate: LocalDateTime,
                         val username:String?, val usrImage:String?)



