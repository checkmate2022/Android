package com.example.avatwin.Fragment.Board

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.avatwin.Auth.App
import com.example.avatwin.R
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.Converter.LocalDateTimeConverter
import com.example.avatwin.DataClass.*
import com.example.avatwin.Service.BoardService
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.fragment_board.view.*
import kotlinx.android.synthetic.main.fragment_board.view.board_delete
import kotlinx.android.synthetic.main.fragment_board_register.*
import kotlinx.android.synthetic.main.fragment_board_update.view.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDateTime

class BoardUpdateFragment: Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_board_update, container, false)


        //init
        initBoard()

        //수정
        root.write_complete.setOnClickListener{
            updateBoard()
        }


        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initBoard(){
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
        var tests = apiService.get_BoardById(App.prefs.boardSeq!!.toLong())
        tests.enqueue(object : Callback<boardGetBodyById> {
            override fun onResponse(call: Call<boardGetBodyById>, response: Response<boardGetBodyById>) {
                if (response.isSuccessful) {
                    var mList = response.body()!!

                    //제목
                    write_title.setText(mList.data.title)

                    //내용
                    write_content.setText(mList.data.content)

                } }

            override fun onFailure(call: Call<boardGetBodyById>, t: Throwable) {
                Log.e("team", "OnFailuer+${t.message}")
            } })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateBoard(){

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


        var data = boardReqBody(write_title.text.toString(), write_content.text.toString())
        apiService.put_board(App.prefs.boardSeq!!.toLong(), data).enqueue(object : Callback<boardGetBodyById> {
            override fun onResponse(call: Call<boardGetBodyById>, response: Response<boardGetBodyById>) {
                val result = response.body()
                Log.e("D", result.toString())
                val fragmentA = BoardDetailFragment()
                val bundle = Bundle()
                fragmentA.arguments = bundle
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.add(R.id.container, fragmentA)
                transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                transaction.commit()

            }

            override fun onFailure(call: Call<boardGetBodyById>, t: Throwable) {
                Log.e("avatar_create", "OnFailuer+${t.message}")
            }
        })

    }
}