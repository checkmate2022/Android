package com.example.avatwin.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.avatwin.Auth.App
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.DataClass.fcmReqBody
import com.example.avatwin.DataClass.fcmResBody
import com.example.avatwin.DataClass.teamPostGetBody
import com.example.avatwin.DataClass.teamReqBody
import com.example.avatwin.Fragment.Chat.ChatListFragment
import com.example.avatwin.Fragment.FcmFragment
import com.example.avatwin.Fragment.HomeFragment
import com.example.avatwin.Fragment.MyPageFragment
import com.example.avatwin.R
import com.example.avatwin.Service.FcmService
import com.example.avatwin.Service.TeamService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_team_register.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class MainActivity : AppCompatActivity() {
    var msg =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            msg = token.toString()
            Log.e("FCM", msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })

        registerFcmToken(msg);

        
        Log.e("Auth", App.prefs.userId.toString())
        with(supportFragmentManager.beginTransaction()) {
            val fragment5 = HomeFragment()
            replace(R.id.container, fragment5)
            commit()
        }

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.tab1 -> {
                    with(supportFragmentManager.beginTransaction()) {
                        val fragment5 = HomeFragment()
                        replace(R.id.container, fragment5)
                        commit()
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.tab2 -> {
                    with(supportFragmentManager.beginTransaction()) {
                        val fragment5 = ChatListFragment()
                        replace(R.id.container, fragment5)
                        commit()
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.tab3 -> {
                    with(supportFragmentManager.beginTransaction()) {
                        val fragment5 = FcmFragment()
                        replace(R.id.container, fragment5)
                        commit()
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.tab4 -> {
                    with(supportFragmentManager.beginTransaction()) {
                        val fragment4 = MyPageFragment()
                        replace(R.id.container, fragment4)
                        commit()
                    }
                    return@setOnNavigationItemSelectedListener true
                }


            }
            return@setOnNavigationItemSelectedListener false
        }

    }

    fun registerFcmToken(token:String){
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(FcmService.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(FcmService::class.java)

        //var data = fcmReqBody(register_id.text.toString(),register_pwd.text.toString(),items)
        apiService.post_fcmToken(token).enqueue(object : Callback<fcmResBody> {
            override fun onResponse(call: Call<fcmResBody>, response: Response<fcmResBody>) {
                val result = response.body()
                Log.e("D",result.toString())

            }

            override fun onFailure(call: Call<fcmResBody>, t: Throwable) {
                Log.e("fcm", "OnFailuer+${t.message}")
            }
        })
    }
}