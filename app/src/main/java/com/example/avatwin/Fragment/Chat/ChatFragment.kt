package com.example.avatwin.Fragment.Chat

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.beust.klaxon.Klaxon
import com.bumptech.glide.Glide
import com.example.avatwin.Adapter.Avatar.avatarAdapter
import com.example.avatwin.Auth.*
import com.example.avatwin.Constant
import com.example.avatwin.DataClass.Chat
import com.example.avatwin.DataClass.avatarBody
import com.example.avatwin.DataClass.chatMessageGetBody
import com.example.avatwin.DataClass.myAvatarRes
import com.example.avatwin.R
import com.example.avatwin.Service.AvatarService
import com.example.avatwin.Service.ChatService
import com.example.testchat.adapter.ChatAdapter
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.google.gson.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_member_search.view.*
import kotlinx.android.synthetic.main.fragment_avatar_register.view.*
import kotlinx.android.synthetic.main.fragment_board_list.view.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.fragment_chat_list.view.*
import kotlinx.android.synthetic.main.fragment_mypage.view.*
import kotlinx.android.synthetic.main.fragment_team_register.*
import kotlinx.android.synthetic.main.item_chat_list.view.*
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ChatFragment: Fragment() {

    lateinit var cAdapter: ChatAdapter

    var jsonObject = JSONObject()
    val constant: Constant = Constant
    lateinit var stompConnection: Disposable
    lateinit var topic: Disposable
    var receiverImage=""
    var receiver=""
    var selectEmoticonUrl=""
    var url1=""
    var url2=""
    var url3=""
    var url4=""
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.fragment_chat, container, false)

        val bundle = arguments

        if(bundle != null) {
            constant.set(App.prefs.userId.toString(), bundle.getString("roomId")!!)
            root.chat_sender.text=bundle.getString("receiver")
            receiver= bundle.getString("receiver")!!
            receiverImage = bundle.getString("receiverImage")!!
            //Log.e("chatIntent",bundle.getString("roomId")!!)
        }


        cAdapter = ChatAdapter(requireContext())
        root.recycler_chat.adapter = cAdapter
        root.recycler_chat.layoutManager = LinearLayoutManager(requireContext())
        root.recycler_chat.setHasFixedSize(true)

        root.emoticon_add_button.setOnClickListener{
            root.chat_emoticon_group.isVisible=true
            //기본 아바타 이모티콘 설정해주기
            setEmoticon()
            //이모티콘 클릭 시 채팅에
            selectEmoticon()
        }


        //원래 채팅기록 가져오기
        init_chat()

        //채팅
        //1. STOMP init
        // url: ws://[도메인]/[엔드포인트]/websocket
        val url = constant.URL
        val intervalMillis = 5000L
        val client = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()

        val stomp = StompClient(client, intervalMillis).apply { this@apply.url = url }

        // 2. connect
        stompConnection = stomp.connect().subscribe {
            when (it.type) {
                Event.Type.OPENED -> {
                    // subscribe 채널구독
                    // 메세지 받아오기
                    Log.e("web","open")
                    topic = stomp.join("/chat/sub/room/" + constant.CHATROOM_ID).subscribe{
                            stompMessage ->
                        val result = Klaxon()
                            .parse<Chat>(stompMessage)
                            requireActivity().runOnUiThread {
                            if (result != null) {
                                //imgae
                                cAdapter.addItem(result,receiverImage)
                                root.recycler_chat.smoothScrollToPosition(cAdapter.itemCount)
                            }
                        }
                    }

                    // 처음 입장
                    try {
                        jsonObject.put("type", "ENTER")
                        jsonObject.put("roomId", constant.CHATROOM_ID)
                        jsonObject.put("sender", constant.SENDER)
                        jsonObject.put("fileUrl",selectEmoticonUrl)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    stomp.send("/chat/pub/message", jsonObject.toString()).subscribe()

                    root.send.setOnClickListener {
                        root.chat_emoticon_group.visibility=View.GONE
                        try {
                            jsonObject.put("type", "TALK")
                            jsonObject.put("roomId", constant.CHATROOM_ID)
                            jsonObject.put("sender", constant.SENDER)
                            jsonObject.put("message", root.message.text.toString())
                            jsonObject.put("fileUrl",selectEmoticonUrl)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        // send
                        stomp.send("/chat/pub/message", jsonObject.toString()).subscribe()
                        root.message.text = null
                    }
                    // unsubscribe
                    //topic.dispose()
                }
                Event.Type.CLOSED -> {

                }
                Event.Type.ERROR -> {
                    Log.e("web","err")
                }
            }
        }
        return root
    }

    fun init_chat(){

        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(ChatService.API_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
        var apiService = retrofit.create(ChatService::class.java)
        var tests = apiService.get_chatMessage(constant.CHATROOM_ID)
        tests.enqueue(object : Callback<chatMessageGetBody> {
            override fun onResponse(call: Call<chatMessageGetBody>, response: Response<chatMessageGetBody>) {
                if (response.isSuccessful) {
                    var mList = response.body()!!

                    for( i: Int in 0..mList.list.size-1) {
                        if (mList.list[i].type != "ENTER") {
                            cAdapter.chatDatas.add(mList.list[i])
                        } } } }

            override fun onFailure(call: Call<chatMessageGetBody>, t: Throwable) {
                Log.e("teamDialog", "OnFailuer+${t.message}") } })
    }

    fun setEmoticon(){
        val defaultImage = R.drawable.profile_none
        //기본아바타
        val url = "https://cdn.pixabay.com/photo/2021/08/03/07/03/orange-6518675_960_720.jpg"


        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit2 = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(AvatarService.API_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        var apiService2 = retrofit2.create(AvatarService::class.java)
        var myavatar = apiService2.get_myAvatar()

        myavatar.enqueue(object : Callback<myAvatarRes> {
            override fun onResponse(call: Call<myAvatarRes>, response: Response<myAvatarRes>) {
                if (response.isSuccessful) {
                    var res = response.body()!!
                    Log.e("avatar", res.toString())

                    for(i: avatarBody in res.list){
                        Log.e("avatar", i.isBasic.toString())
                        if(i.isBasic==true) {
                            Log.e("avatar", i.emoticons[0].emoticonUrl!!)
                            url1= i.emoticons[0].emoticonUrl!!
                            url2= i.emoticons[1].emoticonUrl!!
                            url3= i.emoticons[2].emoticonUrl!!
                            url4= i.emoticons[3].emoticonUrl!!
                            Glide.with(requireContext())
                                .load(url1) // 불러올 이미지 url
                                .placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
                                .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
                                .fallback(defaultImage) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                                .circleCrop() // 동그랗게 자르기
                                .into(iv1_image)
                            Glide.with(requireContext())
                                .load(url2) // 불러올 이미지 url
                                .placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
                                .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
                                .fallback(defaultImage) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                                .circleCrop() // 동그랗게 자르기
                                .into(iv2_image)

                            Glide.with(requireContext())
                                .load(url3) // 불러올 이미지 url
                                .placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
                                .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
                                .fallback(defaultImage) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                                .circleCrop() // 동그랗게 자르기
                                .into(iv3_image)

                            Glide.with(requireContext())
                                .load(url4) // 불러올 이미지 url
                                .placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
                                .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
                                .fallback(defaultImage) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                                .circleCrop() // 동그랗게 자르기
                                .into(iv4_image)
                        }
                    }
                } }

            override fun onFailure(call: Call<myAvatarRes>, t: Throwable) {
                Log.e("avatar", "OnFailuer+${t.message}")
            } })


    }

    fun selectEmoticon(){
        //이미지 선택
        iv1.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                iv2.setChecked(false)
                iv3.setChecked(false)
                iv4.setChecked(false)
                selectEmoticonUrl = url1
            }
        })
        iv2.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                iv1.setChecked(false)
                iv3.setChecked(false)
                iv4.setChecked(false)
                selectEmoticonUrl = url2
            }
        })
        iv3.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                iv1.setChecked(false)
                iv2.setChecked(false)
                iv4.setChecked(false)
                selectEmoticonUrl = url3
            }
        })
        iv4.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                iv1.setChecked(false)
                iv2.setChecked(false)
                iv3.setChecked(false)
                selectEmoticonUrl = url4
            }

        })
    }

}

