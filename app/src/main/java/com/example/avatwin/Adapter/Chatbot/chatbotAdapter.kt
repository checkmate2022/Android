package com.example.avatwin.Adapter.Chatbot

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.DataClass.Message
import com.example.avatwin.DataClass.myteamGetBody
import com.example.avatwin.DataClass.teamaBody
import com.example.avatwin.Fragment.ChatbotFragment
import com.example.avatwin.R
import com.example.avatwin.Service.ApiService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class chatbotAdapter(private var context:Context, private var messageList: List<Message>) : RecyclerView.Adapter<chatbotAdapter.ChatViewHolder>() {
    lateinit var recyclerView:RecyclerView
    var items : ArrayList<String> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_message_one, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val ChatbotFragment = ChatbotFragment.getInstance()

        val message: String = messageList[position].message
        val isReceived: Boolean = messageList[position].isReceived
        if (isReceived) {
            if(message.contains("싶은 팀")){
                Log.e("chabot",message)

                holder.messageReceive.visibility = View.VISIBLE
                holder.messageSend.visibility = View.GONE
                holder.messageReceive.text = message
                lateinit var adapter: chatbotItemListAdapter
                val layoutManager1 = LinearLayoutManager(context)
                holder.recyclerView.layoutManager = layoutManager1

                var items : ArrayList<teamaBody> = arrayListOf()
                val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
                var retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(ApiService.API_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build()
                var apiService = retrofit.create(ApiService::class.java)
                var tests = apiService.get_myTeam()
                tests.enqueue(object : Callback<myteamGetBody> {
                    override fun onResponse(call: Call<myteamGetBody>, response: Response<myteamGetBody>) {
                        if (response.isSuccessful) {
                            var mList = response.body()!!
                            Log.e("mList",mList.list.toString())
                            //  var items = mList.list
                            items=mList.list
                            adapter = chatbotItemListAdapter(items,context)
                            holder.recyclerView.adapter=adapter
                            holder.recyclerView.visibility=View.VISIBLE

                            adapter.setItemClickListener(object :
                                chatbotItemListAdapter.ItemClickListener {
                                override fun onClick(view: View, position: Int) {
                                    Log.e("team", items[position].toString())
                                    ChatbotFragment!!.clickTeamName(items[position])

                                }
                            })
                        } }

                    override fun onFailure(call: Call<myteamGetBody>, t: Throwable) {
                        Log.e("team", "OnFailuer+${t.message}")
                    } })

               } else if(message.contains("일정 종류")){


            }
            else
            {

                 holder.messageReceive.visibility = View.VISIBLE
               holder.messageSend.visibility = View.GONE
               holder.recyclerView.visibility=View.GONE
              holder.messageReceive.text = message
            }
        } else {
            holder.recyclerView.visibility=View.GONE
            holder.messageSend.visibility = View.VISIBLE
            holder.messageReceive.visibility = View.GONE
            holder.messageSend.text = message
        }
    }

    override fun getItemCount(): Int {
        return messageList.count()
    }


    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageReceive: TextView = itemView.findViewById(R.id.message_receive)
        var messageSend: TextView = itemView.findViewById(R.id.message_send)
        var recyclerView:RecyclerView = itemView.findViewById(R.id.recyclerview_chatbot)

    }

}