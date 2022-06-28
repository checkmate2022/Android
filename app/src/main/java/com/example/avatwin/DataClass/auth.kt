package com.example.avatwin.DataClass


data class loginReqBody(val id:String?,val password:String?)
data class loginGetBody(val success:String?,val code:Long?, val msg:String?,val data:String?)


