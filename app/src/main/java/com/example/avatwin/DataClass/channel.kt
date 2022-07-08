package com.example.avatwin.DataClass

//팀생성
data class channelReqBody(val teamId:Long?,val channelName:String?)
data class channelGetBody(val data:channelBody)
data class channelBody(val channelSeq:Long?, val channelName:String?)

//팀별 채널조회
data class channelTeamGetBody(val list:ArrayList<channelBody>)
