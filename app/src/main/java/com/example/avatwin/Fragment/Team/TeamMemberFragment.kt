package com.example.avatwin.Fragment.Team

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.avatwin.Adapter.Team.teamMemberAdapter
import com.example.avatwin.Auth.App
import com.example.avatwin.R
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.DataClass.teamUserGetBody
import com.example.avatwin.Service.TeamService
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_team_main.view.*
import kotlinx.android.synthetic.main.menubar_team.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class TeamMemberFragment(): Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_team_member, container, false)

        /*
        root.menu_button.setOnClickListener {
            main_drawer_layout.openDrawer((GravityCompat.START))
        }*/

        //layoutmanager설정
        val layoutManager = GridLayoutManager(activity,2)
        root.recyclerView_team.layoutManager = layoutManager
        lateinit var adapter: teamMemberAdapter
        adapter = teamMemberAdapter()

        //참여자 가져오기
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(TeamService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(TeamService::class.java)
        apiService.get_teamUser(App.prefs.teamSeq!!.toLong()).enqueue(object : Callback<teamUserGetBody> {
            override fun onResponse(call: Call<teamUserGetBody>, response: Response<teamUserGetBody>) {
                val result = response.body()

                val len: Int = result!!.list.size
                for(i in 0..len-1){
                    adapter.addItem(result!!.list[i])
                    Log.e("userimage", result!!.list[i].toString())
                    }
                root.recyclerView_team.adapter = adapter

          }

            override fun onFailure(call: Call<teamUserGetBody>, t: Throwable) {
                Log.e("team", "OnFailuer+${t.message}")
            }
        })


        return root
    }
}