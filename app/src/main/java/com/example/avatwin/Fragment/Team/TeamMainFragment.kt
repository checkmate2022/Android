package com.example.avatwin.Fragment.Team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.avatwin.Adapter.teamAdapter
import com.example.avatwin.Auth.App
import com.example.avatwin.DataClass.myteamGetBody
import com.example.avatwin.Service.ApiService
import com.example.avatwin.R
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.DataClass.teamaBody
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_team_main.*
import kotlinx.android.synthetic.main.fragment_team_main.view.*
import kotlinx.android.synthetic.main.menubar_team.*
import kotlinx.android.synthetic.main.menubar_team.view.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TeamMainFragment(val teamBody: teamaBody) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.menubar_team, container, false)

        //메뉴바
        root.menu_button.setOnClickListener {
            main_drawer_layout.openDrawer((GravityCompat.START))
        }

        //팀 관리페이지로 이동
        root.team_update_button.setOnClickListener {
            val fragmentA = TeamUpdateFragment(teamBody)
            val bundle = Bundle()
            fragmentA.arguments=bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container,fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }


        //팀 참여자페이지로 이동
        root.team_member_button.setOnClickListener {
            val fragmentA = TeamMemberFragment(teamBody)
            val bundle = Bundle()
            fragmentA.arguments=bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container,fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }
        return root
    }
}