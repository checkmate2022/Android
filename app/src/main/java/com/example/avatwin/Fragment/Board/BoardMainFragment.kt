package com.example.avatwin.Fragment.Board

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
import com.example.avatwin.Service.BoardService
import com.google.gson.*
import kotlinx.android.synthetic.main.fragment_board_list.view.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

class BoardMainFragment: Fragment() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_board_list, container, false)

        root.btn_write.setOnClickListener {
            val fragmentA = BoardRegisterFragment()
            val bundle = Bundle()
            fragmentA.arguments=bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }

        //layoutmanager설정
        val layoutManager = LinearLayoutManager(requireActivity())
        root.recyclerView_board_more.layoutManager = layoutManager
        lateinit var adapter: boardAdapter


        val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeConverter()).create()


        //채널별 게시글 가져오기
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BoardService.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        var apiService = retrofit.create(BoardService::class.java)
        var tests = apiService.get_Board(App.prefs.teamSeq!!.toLong())
        tests.enqueue(object : Callback<boardTeamGetBody> {
            override fun onResponse(call: Call<boardTeamGetBody>, response: Response<boardTeamGetBody>) {
                if (response.isSuccessful) {
                    var mList = response.body()!!
                    Log.e("board-date",mList.list.toString())
                    adapter = boardAdapter()
                    adapter.items=mList.list
                    root.recyclerView_board_more.adapter= adapter

                    adapter.setItemClickListener(object : boardAdapter.ItemClickListener {
                        override fun onClick(view: View, position: Int) {


                            App.prefs.boardSeq=mList.list[position].boardSeq.toString()
                            //Log.e("boardSeq",mList.list[position].baordSeq.toString())
                            val fragmentA = BoardDetailFragment()
                            val bundle = Bundle()
                            fragmentA.arguments=bundle
                            val transaction = requireActivity().supportFragmentManager.beginTransaction()
                            transaction.add(R.id.container,fragmentA)
                            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                            transaction.commit()

                        }
                    })

                } }

            override fun onFailure(call: Call<boardTeamGetBody>, t: Throwable) {
                Log.e("team", "OnFailuer+${t.message}")
            } })

        return root
    }



}

