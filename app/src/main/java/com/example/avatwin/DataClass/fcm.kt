package com.example.avatwin.DataClass

import java.time.LocalDateTime


//알람 목록 조회
data class fcmResBody(val list:ArrayList<fcmBody>)
data class fcmBody(val body:String,val title:String, val notificationDate:LocalDateTime, val userId:String)