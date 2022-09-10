package com.example.avatwin.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.avatwin.Adapter.Team.teamAdapter
import com.example.avatwin.Auth.App
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.DataClass.*
import com.example.avatwin.Fragment.Team.TeamMainFragment
import com.example.avatwin.Fragment.Team.TeamRegisterFragment
import com.example.avatwin.R
import com.example.avatwin.Service.ApiService
import com.example.avatwin.Service.FcmService
import com.example.avatwin.Service.TeamService
import kotlinx.android.synthetic.main.fragment_fcm_list.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_team_register.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class FcmFragment:Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_fcm_list, container, false)

        //val input = QueryInput.newBuilder()

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
            })}

        return root
    }
}