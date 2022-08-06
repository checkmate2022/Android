package com.example.avatwin.DataClass

import java.time.LocalDateTime

//알람보내기
data class fcmReqBody(val userId:String?,val title:String?,val body:String?)
data class fcmResBody(val success:String)