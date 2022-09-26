package com.example.avatwin.Service

import com.example.avatwin.DataClass.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File


interface AvatarService {
    companion object{
        public val API_URL = " http://172.20.31.3:8080/api/v1/"
        public val API_URL2 = " http://172.20.31.3:5000/"
    }

    //캐릭터 등록
    @Multipart
    @POST("avatar")
    fun post_avatar(
        @Part originfile: MultipartBody.Part,
        @Part createdfile: MultipartBody.Part,
        @Part("avatarName") avatarName: String,
        @Part("avatarDescription") avatarDescription:String ,
        @Part("avatarStyle") avatarStyle: String,
        @Part("avatarStyleId") avatarStyleId: Long,
        @Part("sadEmoticon") sadEmoticon: String,
        @Part("happyEmoticon") happyEmoticon: String,
        @Part("winkEmoticon") winkEmoticon: String,
        @Part("angryEmoticon") angryEmoticon: String,
    ): Call<myAvatarRes>

    //캐릭터 수정
    @Multipart
    @PUT("avatar/{avatarId}")
    fun put_avatar(
            @Path("avatarId") avatarId:Long,
            @Part originfile: MultipartBody.Part,
            @Part createdfile: MultipartBody.Part,
            @Part("avatarName") avatarName: String,
            @Part("avatarDescription") avatarDescription: String,
            @Part("avatarStyle") avatarStyle: String,
            @Part("avatarStyleId") avatarStyleId: Long,
    ): Call<myAvatarRes>

    //사용자별 캐릭터 조회
    @GET("avatar/user")
    fun get_myAvatar():Call<myAvatarRes>

    //아이디로 단건 캐릭터 조회
    @GET("avatar/{avatarId}")
    fun get_avatarId(@Path("avatarId") avatarId:Long):Call<avatarGetIdRes>

    //아바타 변형
    @Multipart
    @POST("uploader")
    fun make_avatar(
        @Part file: MultipartBody.Part,
        @Part("user_id")id: Long
    ): Call<ResponseBody>


    @Multipart
    @GET
    fun FF(
        @Part file: MultipartBody.Part,
        @Part("user_id") id: Long,
    ): Call<ResponseBody>

    //아바타 변형
    @Multipart
    @POST("aa")
    fun make_avatar(
        @Part file: MultipartBody.Part,
        @Part("style")style: String,
        @Part("id")id: Long,
        @Part("name")name: String,

        ): Call<ResponseBody>

    //아바타삭제
    @DELETE("avatar/{avatarId}")
    fun delete_avatar(@Path("avatarId") avatarId:Long):Call<avatarDelRes>

    //아바타 기본설정
    @GET("avatar/isBasic/{avatarId}")
    fun basic_avatar(@Path("avatarId") avatarId:Long):Call<avatarDelRes>

    //이모티콘생성
    @Multipart
    @POST("aa")
    fun make_emoticon(
        @Part file: MultipartBody.Part,

        ): Call<ResponseBody>
}

