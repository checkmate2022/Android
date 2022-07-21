package com.example.avatwin.Fragment.Chat

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.avatwin.Adapter.boardAdapter
import com.example.avatwin.Auth.*
import com.example.avatwin.Converter.LocalDateTimeConverter
import com.example.avatwin.R
import com.example.avatwin.DataClass.boardTeamGetBody
import com.example.avatwin.DataClass.chatUserGetBody
import com.example.avatwin.Fragment.Board.BoardDetailFragment
import com.example.avatwin.Service.BoardService
import com.example.avatwin.Service.ChatService
import com.example.testchat.adapter.roomAdapter
import com.google.gson.*
import kotlinx.android.synthetic.main.fragment_board_list.view.*
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.fragment_chat_list.view.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

class ChatListFragment: Fragment() {
    val bundle: Bundle = Bundle()
    lateinit var adapter: roomAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_chat_list, container, false)

        root.recyclerView_chatlist.layoutManager = LinearLayoutManager(requireContext())

        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
            .baseUrl(ChatService.API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()

        var apiService = retrofit.create(ChatService::class.java)
        apiService.get_userChat().enqueue(object :
            Callback<chatUserGetBody> {
            override fun onResponse(call: Call<chatUserGetBody>, response: Response<chatUserGetBody>) {
                val result = response.body()

                adapter = roomAdapter(result!!.list)
                root.recyclerView_chatlist.adapter= adapter

                adapter.setItemClickListener(object : roomAdapter.ItemClickListener {
                    override fun onClick(view: View, position: Int) {

                        bundle.putString("sender", result.list[position].username1)
                        bundle.putString("roomId",  result.list[position].id)
                        val fragmentA = ChatFragment()
                        fragmentA.arguments=bundle
                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        transaction.add(R.id.container,fragmentA)
                        transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                        transaction.commit()

                    }
                })

            }

            override fun onFailure(call: Call<chatUserGetBody>, t: Throwable) {
                Log.e("team", "OnFailuer+${t.message}")
            }
        })
        return root
    }



}

