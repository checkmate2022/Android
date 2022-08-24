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
import com.example.avatwin.Adapter.commentAdapter
import com.example.avatwin.Auth.*
import com.example.avatwin.Converter.LocalDateTimeConverter
import com.example.avatwin.DataClass.*
import com.example.avatwin.R
import com.example.avatwin.Fragment.MyPageFragment
import com.example.avatwin.Service.AvatarService
import com.example.avatwin.Service.BoardService
import com.example.avatwin.Service.CommentService
import com.google.gson.*
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.fragment_board.view.*
import kotlinx.android.synthetic.main.fragment_board_list.view.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDateTime

class BoardDetailFragment(): Fragment() {
    lateinit var adapter: commentAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_board, container, false)
        val layoutManager = LinearLayoutManager(requireActivity())
        root.recyclerView_board_detail.layoutManager = layoutManager

        //init
        initBoard()

        //수정
        root.board_update.setOnClickListener {
            val fragmentA = BoardUpdateFragment()
            val bundle = Bundle()
            fragmentA.arguments=bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }

        //삭제
        root.board_delete.setOnClickListener{
            deleteBoard()
        }

        //댓글
        root.comment_button.setOnClickListener{
            registerComment(root.comment_text.text.toString())
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
                    //날짜
                    var date = mList.data.createDate.toString().substring(0,10)+" "+mList.data.createDate.toString().substring(11,16)
                    board_detail_date.setText(date)
                    //제목
                    board_detail_title.setText(mList.data.title)
                    //이미지
                    //board_detail_image
                    //별명
                    board_detail_name.setText(mList.data.username)
                    //내용
                    board_detail_content.setText(mList.data.content)
                    //댓글 리스트
                    adapter = commentAdapter()
                    adapter.items=mList.data.comments
                    recyclerView_board_detail.adapter= adapter
                } }

            override fun onFailure(call: Call<boardGetBodyById>, t: Throwable) {
                Log.e("team", "OnFailuer+${t.message}")
            } })

    }

    fun deleteBoard(){
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BoardService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(BoardService::class.java)

        apiService.delete_board(App.prefs.boardSeq!!.toLong()).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val result = response.body()
                Log.e("성공",result.toString())
                val fragmentA = BoardMainFragment()
                val bundle = Bundle()
                fragmentA.arguments=bundle
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.add(R.id.container,fragmentA)
                transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                transaction.commit()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("team_put", "OnFailuer+${t.message}")
            }
        })

    }


    fun registerComment(content:String){
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(CommentService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(CommentService::class.java)

        apiService.post_comment(App.prefs.boardSeq!!.toLong(),content).enqueue(object : Callback<commentGetBody> {
            override fun onResponse(call: Call<commentGetBody>, response: Response<commentGetBody>) {
                val result = response.body()
                Log.e("성공",result.toString())
                adapter.addItem(result!!.data)
            }

            override fun onFailure(call: Call<commentGetBody>, t: Throwable) {
                Log.e("team_put", "OnFailuer+${t.message}")
            }
        })

    }
}

