package com.example.avatwin.Fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.avatwin.Adapter.Team.teamSearchListAdapter
import com.example.avatwin.Adapter.fcmAdapter
import com.example.avatwin.Auth.App
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.Converter.LocalDateTimeConverter
import com.example.avatwin.DataClass.fcmBody
import com.example.avatwin.DataClass.fcmResBody
import com.example.avatwin.DataClass.joinGetBody
import com.example.avatwin.DataClass.userGetBody2
import com.example.avatwin.Fragment.Team.TeamRegisterFragment
import com.example.avatwin.R
import com.example.avatwin.Service.FcmService
import com.example.avatwin.Service.UserService
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.dialog_member_search.view.*
import kotlinx.android.synthetic.main.fragment_fcm.view.*
import kotlinx.android.synthetic.main.fragment_team_register.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

class FcmFragment:Fragment() {

    init{ instance = this }

    companion object{
        private var instance: FcmFragment? = null
        fun getInstance(): FcmFragment?
        { return instance  }}


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_fcm, container, false)


        val layoutManager1 = LinearLayoutManager(activity)
        root.recyclerView_fcm.layoutManager = layoutManager1
        lateinit var adapter: fcmAdapter
        adapter = fcmAdapter()

        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeConverter()).create()

        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(FcmService.API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()

        var apiService = retrofit.create(FcmService::class.java)
        var tests = apiService.get_fcm()
        tests.enqueue(object : Callback<fcmResBody> {
            override fun onResponse(call: Call<fcmResBody>, response: Response<fcmResBody>) {
                if (response.isSuccessful) {
                    var mList = response.body()!!
                    for (i: fcmBody in mList.list) {
                            adapter.addItem(i)
                    }

                    root.recyclerView_fcm.adapter = adapter

                } }
            override fun onFailure(call: Call<fcmResBody>, t: Throwable) {
                Log.e("teamDialog", "OnFailuer+${t.message}")
            }
        })

        return root
    }
}

//val input = QueryInput.newBuilder()
/* 알람 테스트
        root.test_fcm.setOnClickListener {
            val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
            var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(FcmService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create()).build()

            var apiService = retrofit.create(FcmService::class.java)

            var data = fcmReqBody(App.prefs.userId,"제목","내용")
            apiService.post_fcm(data).enqueue(object : Callback<fcmResBody> {
                override fun onResponse(call: Call<fcmResBody>, response: Response<fcmResBody>) {
                    val result = response.body()
                    Log.e("D",result.toString())

                }

                override fun onFailure(call: Call<fcmResBody>, t: Throwable) {
                    Log.e("fcm", "OnFailuer+${t.message}")
                }
            })}*/