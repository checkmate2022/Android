package com.example.avatwin.Activities.Login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.avatwin.DataClass.joinGetBody
import com.example.avatwin.DataClass.joinReqBody
import com.example.avatwin.DataClass.userCheckBody
import com.example.avatwin.R
import com.example.avatwin.Service.UserService
import kotlinx.android.synthetic.main.activity_login_register.*
import kotlinx.android.synthetic.main.dialog_member_search.view.*
import kotlinx.android.synthetic.main.dialog_register.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginRegisterActivity : AppCompatActivity() {
    var idValidation = false
    var pwValidation = false
    var nicknameValidation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)


        var retrofit = Retrofit.Builder().baseUrl(UserService.API_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
        var apiService = retrofit.create(UserService::class.java)

        //아이디, 비밀번호 확인, 닉네임 중복검사

        //아이디 확인
        register_id_check_btn.setOnClickListener(View.OnClickListener {
            if (register_id.getText().toString() == "") {
                register_id_check_txt.setText("아이디를 입력해주세요.")
            } else {
                checkId() //id중복확인
            }
        })
        //닉네임 확인
        register_nickname_check_btn.setOnClickListener(View.OnClickListener {
            if (register_nickname.getText().toString() == "") {
                register_nickname_check_txt.setText("닉네임을 입력해주세요.")
            } else {
                checkNickname() //nickname중복확인
            }
        })
        //비밀번호 확인 같은지
        checkPwd()


        //회원가입 버튼누르면
        resup.setOnClickListener {

           var dlg = AlertDialog.Builder(this@LoginRegisterActivity)
            var dialogView = View.inflate(this@LoginRegisterActivity, R.layout.dialog_register, null)
            dlg.setView(dialogView)
            dlg.setPositiveButton("확인") { dialog, which ->

            }
           // dlg.show()
            var seachtext = dialogView.content
            if (register_id.text.length <= 0 || register_pwd.text.length <= 0 || register_nickname.text.length <= 0
                || !idValidation || !pwValidation
            ) {

                if (register_id.text.length == 0) {
                    seachtext.text="아이디를 입력해주세요."
                    dlg.show()

                }
                if (register_pwd.text.length == 0) {
                    seachtext.text="비밀번호를 입력해주세요."
                    dlg.show()

                }
                if (register_nickname.text.length == 0) {
                    seachtext.text="닉네임을 입력해주세요."
                    dlg.show()

                }
                if (!idValidation) {
                    seachtext.text="아이디 중복 확인해주세요."
                    dlg.show()

                }
                if (!pwValidation) {
                    seachtext.text="비밀번호가 일치하지 않습니다."
                    dlg.show()

                }
                if (!nicknameValidation) {
                    seachtext.text="닉네임 중복 확인해주세요."
                    dlg.show()

                }
            } else {

                //아이디 확인
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
        }}
    }

    fun checkId() {

        var retrofit = Retrofit.Builder().baseUrl(UserService.API_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        var apiService = retrofit.create(UserService::class.java)

        var join = apiService.check_userId(register_id.getText().toString())

        join.enqueue(object : Callback<userCheckBody> {
            override fun onResponse(call: Call<userCheckBody>, response: Response<userCheckBody>) {

                val result = response.body()
                if (result!!.data.toInt() == 0) {
                    register_id_check_txt.setText("사용 가능한 아이디입니다.")
                    idValidation = true
                }

            }

            override fun onFailure(call: Call<userCheckBody>, t: Throwable) {
                register_id_check_txt.setText("이미 아이디가 존재합니다.")
                Log.e("fail",t.message.toString())
                idValidation = false
            }
        })
    }

    fun checkNickname() {

        var retrofit = Retrofit.Builder().baseUrl(UserService.API_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        var apiService = retrofit.create(UserService::class.java)

        var join = apiService.check_userName(register_nickname.getText().toString())

        join.enqueue(object : Callback<userCheckBody> {
            override fun onResponse(call: Call<userCheckBody>, response: Response<userCheckBody>) {

                val result = response.body()
                if (result!!.data.toInt() == 0) {
                    register_nickname_check_txt.setText("사용 가능한 닉네임입니다.")
                    nicknameValidation = true
                }

            }

            override fun onFailure(call: Call<userCheckBody>, t: Throwable) {
                register_nickname_check_txt.setText("이미 닉네임이 존재합니다.")
                nicknameValidation = false
            }
        })
    }

    fun checkPwd() { //비밀번호와 비밀번호확인 일치체크
        register_pwdcheck.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                    if (register_pwdcheck.isFocusable() && s.toString() == register_pwd.getText().toString()) {
                        register_pwdcheck_txt.setText("비밀번호를 올바르게 입력했습니다.")
                        pwValidation = true
                    } else {
                        register_pwdcheck_txt.setText("비밀번호를 다시 확인해주세요.")
                        pwValidation = false
                    }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }
}



