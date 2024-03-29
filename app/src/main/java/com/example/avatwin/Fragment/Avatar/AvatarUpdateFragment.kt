package com.example.avatwin.Fragment.Avatar

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.avatwin.Auth.App
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.DataClass.avatarDelRes
import com.example.avatwin.DataClass.avatarGetIdRes
import com.example.avatwin.DataClass.myAvatarRes
import com.example.avatwin.DataClass.myteamGetBody
import com.example.avatwin.Fragment.HomeFragment
import com.example.avatwin.Fragment.MyPageFragment
import com.example.avatwin.R
import com.example.avatwin.Service.AvatarService
import com.example.avatwin.Service.TeamService
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_avatar_register.*
import kotlinx.android.synthetic.main.fragment_avatar_register.avatar_camera
import kotlinx.android.synthetic.main.fragment_avatar_register.avatar_created_button
import kotlinx.android.synthetic.main.fragment_avatar_register.avatar_created_image
import kotlinx.android.synthetic.main.fragment_avatar_register.avatar_gallery
import kotlinx.android.synthetic.main.fragment_avatar_register.avatar_origin_image
import kotlinx.android.synthetic.main.fragment_avatar_register.register_avatar_description
import kotlinx.android.synthetic.main.fragment_avatar_register.register_avatar_name
import kotlinx.android.synthetic.main.fragment_avatar_register.register_avatar_style
import kotlinx.android.synthetic.main.fragment_avatar_register.register_avatar_styleid
import kotlinx.android.synthetic.main.fragment_avatar_update.*
import kotlinx.android.synthetic.main.fragment_avatar_update.view.*
import kotlinx.android.synthetic.main.fragment_mypage.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class AvatarUpdateFragment(val avatarSeq: Long)  : Fragment(){
    private val OPEN_GALLERY = 1
    private val REQUEST_IMAGE_CAPTURE = 2
    private val GALLERY_ACCESS_REQUEST_CODE = 1001
    var profileUri: Uri? = null
    lateinit var currentPhotoPath : String //문자열 형태의 사진 경로값 (초기값을 null로 시작하고 싶을 때 - lateinti var)
    val REQUEST_IMAGE_PICK = 10
    var m_imageFile: File? = null
    var creadImageFile: InputStream? = null
    var c_imageFile: File? = null

    var requestBodyProfile2:RequestBody?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_avatar_update, container, false)

        //기존 아바타 정보 가져오기
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(AvatarService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(AvatarService::class.java)


        apiService.get_avatarId(avatarSeq).enqueue(object : Callback<avatarGetIdRes> {
            override fun onResponse(call: Call<avatarGetIdRes>, response: Response<avatarGetIdRes>) {
                val result = response.body()

                Glide.with(view!!).load(result!!.data.avatarCreatedUrl).into(root.avatar_created_image)
                Glide.with(requireContext())
                    .load(result!!.data.emoticons[0].emoticonUrl)
                    .into(root.re_i1)
                Glide.with(requireContext())
                    .load(result!!.data.emoticons[1].emoticonUrl)
                    .into(root.re_i2)

                Glide.with(requireContext())
                    .load(result!!.data.emoticons[2].emoticonUrl)
                    .into(root.re_i3)

                Glide.with(requireContext())
                    .load(result!!.data.emoticons[3].emoticonUrl)
                    .into(root.re_i4)

                root.created_avatar_address.setText(result!!.data.avatarCreatedUrl)
                root.sad_emoticon_address.setText(result!!.data.emoticons[0].emoticonUrl)
                root.happy_emoticon_address.setText(result!!.data.emoticons[1].emoticonUrl)
                root.wink_emoticon_address.setText(result!!.data.emoticons[2].emoticonUrl)
                root.angry_emoticon_address.setText(result!!.data.emoticons[3].emoticonUrl)


            }

            override fun onFailure(call: Call<avatarGetIdRes>, t: Throwable) {
                Log.e("avatar_create", "OnFailuer+${t.message}")
            }
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}

