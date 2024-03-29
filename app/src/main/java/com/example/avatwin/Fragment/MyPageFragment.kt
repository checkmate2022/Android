package com.example.avatwin.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.avatwin.Activities.Login.LoginActivity
import com.example.avatwin.Activities.Login.LoginRegisterActivity
import com.example.avatwin.Adapter.Avatar.avatarAdapter
import com.example.avatwin.R
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.DataClass.avatarBody
import com.example.avatwin.DataClass.avatarDelRes
import com.example.avatwin.DataClass.myAvatarRes
import com.example.avatwin.DataClass.userGetBody
import com.example.avatwin.Fragment.Avatar.AvatarRegisterFragment
import com.example.avatwin.Fragment.Avatar.AvatarUpdateFragment
import com.example.avatwin.Fragment.Team.TeamRegisterFragment
import com.example.avatwin.Service.AvatarService
import com.example.avatwin.Service.UserService
import kotlinx.android.synthetic.main.fragment_mypage.*
import kotlinx.android.synthetic.main.fragment_mypage.view.*
import kotlinx.android.synthetic.main.fragment_team_register.*
import kotlinx.android.synthetic.main.item_avatar.view.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class MyPageFragment  : Fragment(){
    init{ instance = this }

    companion object{
        private var instance: MyPageFragment? = null
        fun getInstance(): MyPageFragment?
        { return instance  }}
    lateinit var adapter: avatarAdapter

    fun deleteAvatar(position:Long?){

        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(AvatarService.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(AvatarService::class.java)

        apiService.delete_avatar(position!!).enqueue(object : Callback<avatarDelRes> {
            override fun onResponse(call: Call<avatarDelRes>, response: Response<avatarDelRes>) {
                val result = response.body()
                recyclerView_avatar.adapter!!.notifyDataSetChanged()
            }
            override fun onFailure(call: Call<avatarDelRes>, t: Throwable) {
                Log.e("team_put", "OnFailuer+${t.message}")
            }
        })
    }

    fun basicAvatar(position:Long?,name:String?,imageUrl:String?){
        //아바타이름, 사진변경
        //함수안에
        Log.e("avatar",position!!.toString())
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(AvatarService.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(AvatarService::class.java)

        apiService.basic_avatar(position!!).enqueue(object : Callback<avatarDelRes> {
            override fun onResponse(call: Call<avatarDelRes>, response: Response<avatarDelRes>) {
                val result = response.body()
                Log.e("avatar",result!!.success.toString())
                recyclerView_avatar.adapter!!.notifyDataSetChanged()
                pf_avatar_name.setText(name)
                Glide.with(view!!).load(imageUrl).into(pf_img)
            }
            override fun onFailure(call: Call<avatarDelRes>, t: Throwable) {
                Log.e("team_put", "OnFailuer+${t.message}")
            }
        })



    }

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
                    Glide.with(view!!).load(re.data.userImage).into(root.pf_img)
                    //root.pf_avatar_name
                }
            }
            override fun onFailure(call: Call<userGetBody>, t: Throwable) {
                Log.e("mypage", "OnFailuer+${t.message}")
            } })

        //아바타 조회
        //val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        //val layoutManager = LinearLayoutManager(activity,RecyclerView.HORIZONTAL, false)
        val layoutManager = LinearLayoutManager(requireContext())
        root.recyclerView_avatar.layoutManager = layoutManager


        var retrofit2 = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(AvatarService.API_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
        var apiService2 = retrofit2.create(AvatarService::class.java)
        var myavatar = apiService2.get_myAvatar()

        myavatar.enqueue(object : Callback<myAvatarRes> {
            override fun onResponse(call: Call<myAvatarRes>, response: Response<myAvatarRes>) {
                if (response.isSuccessful) {
                    var re = response.body()!!
                    //Log.e("avata555555555r", re.toString())
                    adapter = avatarAdapter(re.list)
                    for(i: avatarBody in re.list){
                        if(i.isBasic==true){
                            root.pf_avatar_name.setText(i.avatarName)
                        }
                    }

                    root.recyclerView_avatar.adapter= adapter

                    adapter.setItemClickListener(object : avatarAdapter.ItemClickListener {
                        override fun onClick(view: View, position: Int) {


                            val avatarSeq =re.list[position].avatarSeq

                            val fragmentA = AvatarUpdateFragment(avatarSeq!!)
                            val bundle = Bundle()
                            fragmentA.arguments=bundle
                            val transaction = requireActivity().supportFragmentManager.beginTransaction()
                            transaction.add(R.id.container,fragmentA)
                            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                            transaction.commit()

                        }
                    })
                } }

            override fun onFailure(call: Call<myAvatarRes>, t: Throwable) {
                Log.e("avatar", "OnFailuer+${t.message}")
            } })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //아바타 생성페이지로 이동
        av_add.setOnClickListener {
            val fragmentA = AvatarRegisterFragment()
            val bundle = Bundle()
            fragmentA.arguments=bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container,fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }

        //로그아웃, 로그인 페이지로 이동
        logout.setOnClickListener {
            var intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }


}