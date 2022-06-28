package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import retrofit2.Call
import retrofit2.http.*

interface AvatarService {
    companion object{
        public val API_URL = " http://10.0.2.2:8080/api/v1/"
    }

    //캐릭터 등록

    //사용자별 캐릭터 조회
    @GET("avatar/user")
    fun get_myAvatar():Call<myAvatarRes>

}

