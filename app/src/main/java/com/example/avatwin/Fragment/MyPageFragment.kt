package com.example.avatwin.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.avatwin.Adapter.avatarAdapter
import com.example.avatwin.R
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.DataClass.myAvatarRes
import com.example.avatwin.DataClass.userGetBody
import com.example.avatwin.Service.AvatarService
import com.example.avatwin.Service.UserService
import kotlinx.android.synthetic.main.fragment_mypage.*
import kotlinx.android.synthetic.main.fragment_mypage.view.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyPageFragment  : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_mypage, container, false)

        //기본정보가져오기
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(UserService.API_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
        var apiService = retrofit.create(UserService::class.java)

        var tests = apiService.get_user()
        tests.enqueue(object : Callback<userGetBody> {
            override fun onResponse(call: Call<userGetBody>, response: Response<userGetBody>) {
                if (response.isSuccessful) {
                    var re = response.body()!!
                    root.pf_id.setText(re.data.userId.toString())
                    root.pf_nickname.setText(re.data.username.toString())
                }
            }
            override fun onFailure(call: Call<userGetBody>, t: Throwable) {
                Log.e("mypage", "OnFailuer+${t.message}")
            } })



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //캐릭터 가져오기
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        val layoutManager = LinearLayoutManager(activity,RecyclerView.HORIZONTAL, false)
        recyclerView_avatar.layoutManager = layoutManager
        lateinit var adapter: avatarAdapter

        var retrofit2 = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(AvatarService.API_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
        var apiService2 = retrofit2.create(AvatarService::class.java)
        var myavatar = apiService2.get_myAvatar()

        myavatar.enqueue(object : Callback<myAvatarRes> {
            override fun onResponse(call: Call<myAvatarRes>, response: Response<myAvatarRes>) {
                if (response.isSuccessful) {
                    var re2 = response.body()!!
                    Log.e("avatar", re2.toString())
                    adapter = avatarAdapter(re2.list)
                    recyclerView_avatar.adapter= adapter
                } }

            override fun onFailure(call: Call<myAvatarRes>, t: Throwable) {
                Log.e("avatar", "OnFailuer+${t.message}")
            } })

        av_add.setOnClickListener {
            val fragmentA = AvatarRegisterFragment()
            val bundle = Bundle()
            fragmentA.arguments=bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container,fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }
    }


}