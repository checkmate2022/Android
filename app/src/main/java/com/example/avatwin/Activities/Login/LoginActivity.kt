package com.example.avatwin.Activities.Login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.avatwin.Auth.App
import com.example.avatwin.Service.ApiService
import com.example.avatwin.DataClass.loginGetBody
import com.example.avatwin.DataClass.loginReqBody
import com.example.avatwin.Activities.MainActivity
import com.example.avatwin.R
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        var retrofit = Retrofit.Builder().baseUrl(ApiService.API_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
        var apiService = retrofit.create(ApiService::class.java)

        //회원가입으로 이동
        login_register.setOnClickListener {
            var intent2 = Intent(applicationContext, LoginRegisterActivity::class.java)
            startActivity(intent2)
        }

        //로그인
        login_login.setOnClickListener {
            var loginData = loginReqBody(login_id.text.toString(), login_pwd.text.toString())
            var login = apiService.post_login(loginData)

            login.enqueue(object : Callback<loginGetBody> {
                override fun onResponse(call: Call<loginGetBody>, response: Response<loginGetBody>) {
                    if (response.code() == 200) {
                        val result = response.body()
                        var intent = Intent(applicationContext, MainActivity::class.java)
                        App.prefs.token=result!!.data
                        App.prefs.userId=login_id.text.toString()
                        startActivityForResult(intent, 0)
                    }
                }

                override fun onFailure(call: Call<loginGetBody>, t: Throwable) {
                    Log.e("login", "로그인 실패-${t.message}")
                }
            })
        }
    }}



