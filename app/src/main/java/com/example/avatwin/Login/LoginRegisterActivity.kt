package com.example.avatwin.Login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.avatwin.Service.ApiService
import com.example.avatwin.DataClass.joinGetBody
import com.example.avatwin.DataClass.joinReqBody
import com.example.avatwin.R
import com.example.avatwin.Service.UserService
import kotlinx.android.synthetic.main.activity_login_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)


        var retrofit = Retrofit.Builder().baseUrl(UserService.API_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
        var apiService = retrofit.create(UserService::class.java)

        //아이디, 비밀번호 확인, 닉네임 중복검사




        //회원가입
        resup.setOnClickListener {
            var joinData = joinReqBody(register_id.text.toString(),register_nickname.text.toString(),register_pwd.text.toString())
            var join = apiService.post_join(joinData)

            join.enqueue(object : Callback<joinGetBody> {
                override fun onResponse(call: Call<joinGetBody>, response: Response<joinGetBody>) {
                    if (response.code() == 200) {
                        val result = response.body()
                        var intent2 = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent2)
                    }
                }

                override fun onFailure(call: Call<joinGetBody>, t: Throwable) {
                    Log.e("login", "회원가입 실패-${t.message}")
                }
            })
        }
    }}



