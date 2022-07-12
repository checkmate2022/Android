package com.example.avatwin.DataClass

//아바타 등록
data class myAvatarRes(val success:String?,val code:Long?, val msg:String?,val list:ArrayList<avatarBody>)
data class avatarBody(val avatarSeq:Long?,val avatarName:String?,val avatarDescription:String?,
                      val avatarOriginUrl:String?,val avatarCreatedUrl:String?,
                      val avatarStyleId: Long?, val avatarDate: String?, val isBasic: Boolean)

//단건 아바타 조회
data class avatarGetIdRes(val success:String?,val code:Long?, val msg:String?,val data:avatarBody)

//아바타 수정
data class avatarPutRes(val success:String?,val code:Long?, val msg:String?,val data:avatarBody)

//아바타 삭제
data class avatarDelRes(val success:String?,val code:Long?, val msg:String?)
