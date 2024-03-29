package com.example.avatwin.DataClass

import java.time.LocalDateTime

//회원가입
data class joinReqBody(val userId:String?,val username:String?,val password:String?)
data class joinGetBody(val userSeq:Long?,val userId:String?, val username: String?, val userImage:String?,val userImg:String?)

//다른 api에서 user호출시 사용하는 user정보
data class userData(val userSeq:Long?,val userId:String?, val username: String?,val userImage:String?)

//유저정보조회
data class userGetBody(val success:String?,val code:Long?, val msg:String?,val data:joinGetBody)

//유저정보조회
data class userGetBody2(val success:String?,val code:Long?, val msg:String?,val list:ArrayList<joinGetBody>)

//아이디, 닉네임 중복확인
data class userCheckBody(val success:String?,val code:Long?, val msg:String?,val data:Long)