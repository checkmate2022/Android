package com.example.avatwin.DataClass

import java.time.LocalDateTime

//회원가입
data class joinReqBody(val userId:String?,val username:String?,val password:String?)
data class joinGetBody(val userSeq:Long?,val userId:String?, val username: String?,
                       val providerType: String?,val roleType:String?,val createdAt: String?,val modifiedAt: String?,val userImage:String?)

//유저정보조회
data class userGetBody(val success:String?,val code:Long?, val msg:String?,val data:joinGetBody)