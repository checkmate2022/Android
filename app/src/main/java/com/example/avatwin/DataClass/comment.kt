package com.example.avatwin.DataClass

import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

//댓글 생성
data class commentGetBody(val data:commentBody)
data class commentBody(val commentSeq:Long?, val content:String?,val username:String?,val userImage:String?)


//단건 댓글 조회
data class commentGetBodyById(val list:ArrayList<commentBody>)

