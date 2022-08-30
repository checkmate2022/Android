package com.example.avatwin.Fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.avatwin.Adapter.Team.teamMenuAdapter
import com.example.avatwin.Adapter.boardAdapter
import com.example.avatwin.Adapter.channelAdapter
import com.example.avatwin.Auth.*
import com.example.avatwin.Converter.LocalDateTimeConverter
import com.example.avatwin.DataClass.*
import com.example.avatwin.R
import com.example.avatwin.Fragment.Board.BoardMainFragment
import com.example.avatwin.Fragment.Team.TeamMainFragment
import com.example.avatwin.Fragment.Team.TeamRegisterFragment
import com.example.avatwin.Service.*
import com.google.gson.*
import kotlinx.android.synthetic.main.dialog_channel_update.view.*
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.fragment_board_list.view.*
import kotlinx.android.synthetic.main.fragment_channel_update.*
import kotlinx.android.synthetic.main.fragment_channel_update.view.*
import kotlinx.android.synthetic.main.fragment_team_register.*
import kotlinx.android.synthetic.main.menubar_team.view.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDateTime

class ChannelUpdateFragment() : Fragment() {

    init {
        instance = this
    }

    companion object {
        private var instance: ChannelUpdateFragment? = null
        fun getInstance(): ChannelUpdateFragment? {
            return instance
        }
    }

    var channelSeq: Long = 0
    lateinit var adapter: channelAdapter


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.fragment_channel_update, container, false)

        //완료 버튼 누르면 팀메인 페이지로 이동
        root.compelete_button.setOnClickListener {
            val fragmentA = TeamMainFragment()
            val bundle = Bundle()
            fragmentA.arguments = bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle })
                .addToBackStack(null)
            transaction.commit()
        }

        //채널 초기화
        adapter = channelAdapter()
        val layoutManager1 = LinearLayoutManager(activity)

        root.recyclerview_channel.layoutManager = layoutManager1
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(ChannelService.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(ChannelService::class.java)


        apiService.get_teamChannel(App.prefs.teamSeq!!.toLong())
            .enqueue(object : Callback<channelTeamGetBody> {
                override fun onResponse(
                    call: Call<channelTeamGetBody>,
                    response: Response<channelTeamGetBody>
                ) {
                    val result = response.body()!!
                    //Log.e("성공", result.toString())
                   // for (i in 0..result.list.size - 1) {
                        adapter.items = result.list

                    root.recyclerview_channel.adapter = adapter

                }

                override fun onFailure(call: Call<channelTeamGetBody>, t: Throwable) {
                    Log.e("team_put", "OnFailuer+${t.message}")
                }
            })

        return root
    }

    fun deleteChannel(item: channelBody) {

        var dlg = AlertDialog.Builder(requireContext())
        var dialogView = View.inflate(context, R.layout.dialog_delete, null)
        dlg.setView(dialogView)

        dlg.setPositiveButton("확인") { dialog, which ->

            val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
            var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(ChannelService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create()).build()

            var apiService = retrofit.create(ChannelService::class.java)

            apiService.delete_channel(item.channelSeq!!).enqueue(object : Callback<channelRes> {
                override fun onResponse(call: Call<channelRes>, response: Response<channelRes>) {
                    val result = response.body()
                    Log.e("성공",result.toString())
                   // adapter.items[position].channelName= dialogView.channel_title.text.toString()
                    recyclerview_channel.adapter!!.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<channelRes>, t: Throwable) {
                    Log.e("team_put", "OnFailuer+${t.message}")
                }
            })
        }
        dlg.setNegativeButton("취소") { dialog, which -> }
        dlg.show()
    }

    fun updateChannel(item: channelBody, position:Int) {

        var dlg = AlertDialog.Builder(requireContext())
        var dialogView = View.inflate(context, R.layout.dialog_channel_update, null)
        dlg.setView(dialogView)
        dialogView.channel_title.setText(item.channelName)
        dlg.setPositiveButton("확인") { dialog, which ->


            val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
            var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(ChannelService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create()).build()

            var apiService = retrofit.create(ChannelService::class.java)

            apiService.put_channel(item.channelSeq!!, dialogView.channel_title.text.toString()).enqueue(object : Callback<channelGetBody> {
                override fun onResponse(call: Call<channelGetBody>, response: Response<channelGetBody>) {
                    val result = response.body()

                    adapter.items[position].channelName= dialogView.channel_title.text.toString()
                    recyclerview_channel.adapter!!.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<channelGetBody>, t: Throwable) {
                    Log.e("schedule", "OnFailuer+${t.message}")
                }
            })
        }
        dlg.setNegativeButton("취소") { dialog, which -> }
        dlg.show()
    }

}




