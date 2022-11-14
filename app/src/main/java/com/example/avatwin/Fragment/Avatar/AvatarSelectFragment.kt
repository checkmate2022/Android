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


class AvatarSelectFragment(val avatarSeq: Long)  : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_avatar_select, container, false)


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}

