package com.example.avatwin.DataClass


data class teamReqBody(val teamName:String?,val teamDescription:String?,val participantName:ArrayList<String>)
data class teamGetBody(val success:String?,val code:Long?, val msg:String?,val list:ArrayList<teamBody>)

data class teamBody(val teamSeq:Long?,val teamName:String?,val teamDescription:String?)

data class myteamGetBody(val success:String?,val code:Long?, val msg:String?,val list:ArrayList<teamaBody>)
data class teamaBody(val teamSeq:Long?,val user:joinGetBody,val teamName:String?,val teamDescription:String?,val schedule:String?,val participantName: ArrayList<String>)
