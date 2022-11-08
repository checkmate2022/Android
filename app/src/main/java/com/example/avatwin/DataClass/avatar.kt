package com.example.avatwin.DataClass

//아바타 등록
data class myAvatarRes(val success:String?,val code:Long?, val msg:String?,val list:ArrayList<avatarBody>)
data class avatarBody(val avatarSeq:Long?,val avatarName:String?,val avatarDescription:String?,val avatarStyle:String?,
                      val avatarOriginUrl:String?,val avatarCreatedUrl:String?,
                      val avatarStyleId: Long?, val avatarDate: String?, val isBasic: Boolean,
                        val emoticons:ArrayList<emoticonBody>)
//이모티콘
data class emoticonBody(val emoticonSeq:Long?,val emoticonUrl:String?,val emoticonType:String? )

//단건 아바타 조회
data class avatarGetIdRes(val success:String?,val code:Long?, val msg:String?,val data:avatarBody)

//아바타 수정
data class avatarPutRes(val success:String?,val code:Long?, val msg:String?,val data:avatarBody)

//아바타 삭제
data class avatarDelRes(val success:String?,val code:Long?, val msg:String?)

//이모티콘 생성
data class emoticonGetBody(val happy:String?,val sad:String?,val angry:String?,val wink:String?)

