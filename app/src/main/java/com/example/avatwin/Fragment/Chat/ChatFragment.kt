package com.example.avatwin.Fragment.Chat

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.beust.klaxon.Klaxon
import com.example.avatwin.Auth.*
import com.example.avatwin.Constant
import com.example.avatwin.DataClass.Chat
import com.example.avatwin.DataClass.chatUserGetBody
import com.example.avatwin.R
import com.example.avatwin.Service.ChatService
import com.example.testchat.adapter.ChatAdapter
import com.example.testchat.adapter.roomAdapter
import com.google.gson.*
import kotlinx.android.synthetic.main.fragment_board_list.view.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.fragment_chat_list.view.*
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import io.reactivex.disposables.Disposable
class ChatFragment: Fragment() {

    lateinit var cAdapter: ChatAdapter

    var jsonObject = JSONObject()

    lateinit var stompConnection: Disposable
    lateinit var topic: Disposable
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.fragment_chat, container, false)

        val bundle = arguments
        val constant: Constant = Constant
        if(bundle != null) {
            constant.set(bundle.getString("sender")!!, bundle.getString("roomId")!!)
            Log.e("chatIntent",bundle.getString("sender")!!+bundle.getString("roomId")!!)
        }


        cAdapter = ChatAdapter(requireContext())
        root.recycler_chat.adapter = cAdapter
        root.recycler_chat.layoutManager = LinearLayoutManager(requireContext())
        root.recycler_chat.setHasFixedSize(true)

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
                                cAdapter.addItem(result)
                                root.recycler_chat.smoothScrollToPosition(cAdapter.itemCount)
                            }
                        }
                    }

                    // 처음 입장
                    try {
                        jsonObject.put("type", "ENTER")
                        jsonObject.put("roomId", constant.CHATROOM_ID)
                        jsonObject.put("sender", constant.SENDER)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    stomp.send("/chat/pub/message", jsonObject.toString()).subscribe()

                    root.send.setOnClickListener {
                        try {
                            jsonObject.put("type", "TALK")
                            jsonObject.put("roomId", constant.CHATROOM_ID)
                            jsonObject.put("sender", constant.SENDER)
                            jsonObject.put("message", root.message.text.toString())
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



}

