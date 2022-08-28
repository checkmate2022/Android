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
import com.example.avatwin.Service.CommentService
import com.google.gson.GsonBuilder
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

class CommentUpdateFragment(var item: commentBody): Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_comment_update, container, false)


        //init
        //내용
        root.write_content.setText(item.content)

        //수정
        root.write_complete.setOnClickListener{
            updateComment()
        }


        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateComment() {

        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(CommentService.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(CommentService::class.java)

        apiService.put_comment(item.commentSeq!!, write_content.text.toString())
            .enqueue(object : Callback<commentGetBody> {
                override fun onResponse(
                    call: Call<commentGetBody>,
                    response: Response<commentGetBody>
                ) {
                    val result = response.body()
                    val fragmentA = BoardDetailFragment()
                    val bundle = Bundle()
                    fragmentA.arguments = bundle
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.add(R.id.container, fragmentA)
                    transaction.replace(R.id.container, fragmentA.apply { arguments = bundle })
                        .addToBackStack(null)
                    transaction.commit()
                }

                override fun onFailure(call: Call<commentGetBody>, t: Throwable) {
                    Log.e("COMMENT", "OnFailuer+${t.message}")
                }
            })


    }
}