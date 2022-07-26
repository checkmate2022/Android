package com.example.avatwin.Fragment.Board

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.avatwin.Auth.App
import com.example.avatwin.R
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.DataClass.*
import com.example.avatwin.Service.BoardService
import kotlinx.android.synthetic.main.fragment_board_register.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class BoardRegisterFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_board_register, container, false)



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        write_complete.setOnClickListener {
            val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
            var retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BoardService.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create()).build()

            var apiService = retrofit.create(BoardService::class.java)


            var data = boardReqBody(write_title.text.toString(),write_content.text.toString())
            apiService.post_board(App.prefs.channelId!!.toLong(),data).enqueue(object : Callback<boardGetBody> {
                override fun onResponse(call: Call<boardGetBody>, response: Response<boardGetBody>) {
                    val result = response.body()
                    Log.e("D",result.toString())
                    val fragmentA = BoardMainFragment()
                    val bundle = Bundle()
                    fragmentA.arguments=bundle
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.add(R.id.container,fragmentA)
                    transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                    transaction.commit()

                }

                override fun onFailure(call: Call<boardGetBody>, t: Throwable) {
                    Log.e("avatar_create", "OnFailuer+${t.message}")
                }
            })

        }

















    }

}