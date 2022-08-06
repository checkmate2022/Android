package com.example.avatwin.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.avatwin.Auth.App
import com.example.avatwin.Fragment.Chat.ChatListFragment
import com.example.avatwin.Fragment.HomeFragment
import com.example.avatwin.Fragment.MyPageFragment
import com.example.avatwin.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
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
            val msg = token.toString()
            Log.e("FCM", msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })



        
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
}