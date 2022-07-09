package com.example.avatwin.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.avatwin.Adapter.teamAdapter
import com.example.avatwin.DataClass.myteamGetBody
import com.example.avatwin.Service.ApiService
import com.example.avatwin.R
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.Fragment.Team.TeamMainFragment
import com.example.avatwin.Fragment.Team.TeamRegisterFragment
import kotlinx.android.synthetic.main.fragment_home.view.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BoardMainFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_home, container, false)

        //팀생성 페이지로 이동
        root.register_team.setOnClickListener {
            val fragmentA = TeamRegisterFragment()
            val bundle = Bundle()
            fragmentA.arguments=bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container,fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }


        //layoutmanager설정
        val layoutManager = GridLayoutManager(activity,2)
        root.recyclerView_team.layoutManager = layoutManager
        lateinit var adapter: teamAdapter

        //나의 팀 가져오기
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
                    Log.e("team",mList.toString())
                    adapter = teamAdapter(mList.list)
                    root.recyclerView_team.adapter= adapter

                    adapter.setItemClickListener(object : teamAdapter.ItemClickListener {
                        override fun onClick(view: View, position: Int) {

                            Log.e("ddd", "Ss")
                            val teamaBody =mList.list[position]
                            val fragmentA = TeamMainFragment()
                            val bundle = Bundle()
                            fragmentA.arguments=bundle
                            val transaction = requireActivity().supportFragmentManager.beginTransaction()
                            transaction.add(R.id.container,fragmentA)
                            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                            transaction.commit()

                        }
                    })
                } }

            override fun onFailure(call: Call<myteamGetBody>, t: Throwable) {
                Log.e("team", "OnFailuer+${t.message}")
            } })

        return root
    }
}