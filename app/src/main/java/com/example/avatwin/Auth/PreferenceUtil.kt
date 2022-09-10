package com.example.avatwin.Auth

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences =
            context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    var token:String?
        get() = prefs.getString("token",null)
        set(value){
            prefs.edit().putString("token",value).apply()
        }

    var fcmToken:String?
        get() = prefs.getString("fcmToken",null)
        set(value){
            prefs.edit().putString("fcmToken",value).apply()
        }

    var userId:String?
        get() = prefs.getString("userId",null)
        set(value){
            prefs.edit().putString("userId",value).apply()
        }

    var teamSeq:String?
        get() = prefs.getString("teamSeq",null)
        set(value){
            prefs.edit().putString("teamSeq",value).apply()
        }

    var channelId:String?
        get() = prefs.getString("channelId",null)
        set(value){
            prefs.edit().putString("channelId",value).apply()
        }

    var channelName:String?
        get() = prefs.getString("channelName",null)
        set(value){
            prefs.edit().putString("channelName",value).apply()
        }

    var boardSeq:String?
        get() = prefs.getString("boardSeq",null)
        set(value){
            prefs.edit().putString("boardSeq",value).apply()
        }
}