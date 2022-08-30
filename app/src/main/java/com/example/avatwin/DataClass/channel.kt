package com.example.avatwin.DataClass

import io.reactivex.internal.operators.maybe.MaybeDoAfterSuccess

//팀생성
data class channelReqBody(val teamId:Long?,val channelName:String?)
data class channelGetBody(val data:channelBody)
data class channelBody(val channelSeq:Long?, var channelName:String?)

//팀별 채널조회
data class channelTeamGetBody(val list:ArrayList<channelBody>)

//삭제
data class channelRes(val success: String)