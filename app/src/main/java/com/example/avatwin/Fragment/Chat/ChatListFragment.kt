package com.example.avatwin.Fragment.Chat

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.avatwin.Adapter.Team.teamSearchListAdapter
import com.example.avatwin.Adapter.boardAdapter
import com.example.avatwin.Auth.*
import com.example.avatwin.Converter.LocalDateTimeConverter
import com.example.avatwin.DataClass.*
import com.example.avatwin.R
import com.example.avatwin.Fragment.Board.BoardDetailFragment
import com.example.avatwin.Service.BoardService
import com.example.avatwin.Service.ChatService
import com.example.avatwin.Service.UserService
import com.example.testchat.adapter.roomAdapter
import com.google.gson.*
import kotlinx.android.synthetic.main.dialog_member_search.view.*
import kotlinx.android.synthetic.main.fragment_board_list.view.*
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.fragment_chat_list.view.*
import kotlinx.android.synthetic.main.fragment_team_register.*
import kotlinx.android.synthetic.main.item_chat_list.view.*
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
    var sender:String=""
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_chat_list, container, false)

        //채팅방생성
        root.register_room.setOnClickListener {
            registerRoom()
        }

        //채팅방 목록
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
                        if(App.prefs.userId==result.list[position].username1){
                            sender = result.list[position].username2.toString()
                        }
                        else{
                            sender = result.list[position].username1.toString()
                        }
                        Log.e("Sender",sender)
                        bundle.putString("sender", sender)
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

 fun registerRoom(){
     val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()

    //검색 다이얼로그 보이기
     var dlg = AlertDialog.Builder(requireContext())
     var dialogView = View.inflate(context, R.layout.dialog_chat_register, null)
     dlg.setView(dialogView)

     //확인버튼 눌렀을 때 서버에 채팅방 등록
      dlg.setPositiveButton("확인") { dialog, which ->

          var retrofit2 = Retrofit.Builder()
                  .baseUrl(ChatService.API_URL)
                  .client(okHttpClient)
                  .addConverterFactory(GsonConverterFactory.create())
                  .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()

          var apiService2 = retrofit2.create(ChatService::class.java)
          apiService2.post_chat(dialogView.search_edittext.text.toString()).enqueue(object :
                  Callback<chatGetBody> {
              override fun onResponse(call: Call<chatGetBody>, response: Response<chatGetBody>) {
                  val result = response.body()
                    //data update해야함, fragment에서 할까.
                  adapter.addItem(result!!.data)
                  adapter.notifyDataSetChanged()
              }

              override fun onFailure(call: Call<chatGetBody>, t: Throwable) {
                  Log.e("team", "OnFailuer+${t.message}")
              }
          })
      }



     var seachtext = dialogView.search_edittext.getText()


     val layoutManager1 = LinearLayoutManager(activity)
     dialogView.recyclerView_search.layoutManager = layoutManager1
     lateinit var adapter: teamSearchListAdapter
     adapter = teamSearchListAdapter()
    //검색버튼
     dialogView.search_btn.setOnClickListener {

         Log.e("Dd", "ss")

         var retrofit = Retrofit.Builder()
                 .client(okHttpClient)
                 .baseUrl(UserService.API_URL)
                 .addConverterFactory(GsonConverterFactory.create()).build()
         var apiService = retrofit.create(UserService::class.java)
         var tests = apiService.get_user_search(seachtext.toString())
         tests.enqueue(object : Callback<userGetBody2> {
             override fun onResponse(call: Call<userGetBody2>, response: Response<userGetBody2>) {
                 if (response.isSuccessful) {
                     var mList = response.body()!!
                     for (i: joinGetBody in mList.list) {
                         if (i.userId.toString() != App.prefs.userId) {
                             adapter.addItem(i)
                         }
                     }
                     dialogView.recyclerView_search.adapter = adapter


                     adapter.setItemClickListener(object : teamSearchListAdapter.ItemClickListener {
                         override fun onClick(view: View, position: Int) {
                             Log.e("ddd", "Ss")
                             dialogView.search_edittext.setText(mList.list[position].userId.toString())

                         }
                     })
                 }


             }

             override fun onFailure(call: Call<userGetBody2>, t: Throwable) {
                 Log.e("teamDialog", "OnFailuer+${t.message}")
             }
         })}


         dlg.show()


 }


}

