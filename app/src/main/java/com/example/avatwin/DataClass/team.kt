package com.example.avatwin.DataClass

//팀생성
data class teamReqBody(val teamName:String?,val teamDescription:String?,val participantName:ArrayList<String>)
data class teamPostGetBody(val data:teamBody)

//아이디로 팀 조회
data class teamIdGetBody(val data:teamBody)

//아이디로 팀 사용자 조회
data class teamUserGetBody(val list:ArrayList<joinGetBody>)


//나의 팀 조회
data class myteamGetBody(val success:String?,val code:Long?, val msg:String?,val list:ArrayList<teamaBody>)
data class teamaBody(val teamSeq:Long?,val user:joinGetBody,val teamName:String?,val teamDescription:String?,val schedule:String?,val participantName: ArrayList<String>)


//
data class teamGetBody(val success:String?,val code:Long?, val msg:String?,val list:ArrayList<teamBody>)
data class teamBody(val teamSeq:Long?,val teamName:String?,val teamDescription:String?)
