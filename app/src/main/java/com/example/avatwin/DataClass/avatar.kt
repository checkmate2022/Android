package com.example.avatwin.DataClass


data class myAvatarRes(val success:String?,val code:Long?, val msg:String?,val list:ArrayList<avatarBody>)
data class avatarBody(val avatarSeq:Long?,val avatarName:String?,val avatarDescription:String?,
                      val avatarOriginUrl:String?,val avatarCreatedUrl:String?,
                      val avatarStyleId: Long?, val avatarDate: String?, val isBasic: Boolean)


//아바타 변형
