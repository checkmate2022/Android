package com.example.avatwin.Fragment.Team

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.avatwin.Adapter.teamMenuAdapter
import com.example.avatwin.Auth.App
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.DataClass.*
import com.example.avatwin.Decorator.TodayDecorator
import com.example.avatwin.Fragment.Board.BoardMainFragment
import com.example.avatwin.Fragment.Schedule.ScheduleRegisterFragment
import com.example.avatwin.R
import com.example.avatwin.Service.ChannelService
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.android.synthetic.main.dialog_channel_register.view.*
import kotlinx.android.synthetic.main.fragment_team_main.view.*
import kotlinx.android.synthetic.main.menubar_team.*
import kotlinx.android.synthetic.main.menubar_team.view.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class TeamMainFragment() : Fragment() {
    //채널 adapter
    val layoutManager1 = LinearLayoutManager(activity)

    lateinit var adapter: teamMenuAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.menubar_team, container, false)

        lateinit var calendar: MaterialCalendarView

        calendar = root.findViewById(R.id.calendar)
        calendar.setSelectedDate(CalendarDay.today())
        calendar.addDecorator(TodayDecorator())
        calendar.setOnDateChangedListener(object : OnDateSelectedListener {
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
                //여기에 가져온 일정 apater추가
                //calendar.addDecorator(EventDecorator(Collections.singleton(date)))
            }

        })

        //일정추가 페이지로 이동
        root.schedule_add_button.setOnClickListener {
            val fragmentA = ScheduleRegisterFragment()
            val bundle = Bundle()
            fragmentA.arguments=bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }

        //메뉴 초기화
        root.recyclerView_team_menu.layoutManager = layoutManager1
        adapter =teamMenuAdapter()
        adapter.clearItem()
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(ChannelService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(ChannelService::class.java)


        apiService.get_teamChannel(App.prefs.teamSeq!!.toLong()).enqueue(object : Callback<channelTeamGetBody> {
            override fun onResponse(call: Call<channelTeamGetBody>, response: Response<channelTeamGetBody>) {
                val result = response.body()!!
                Log.e("성공",result.toString())
                for(i in 0..result.list.size-1){
                adapter.addItem(result.list[i].channelName.toString())}
                root.recyclerView_team_menu.adapter = adapter

                adapter.setItemClickListener(object : teamMenuAdapter.ItemClickListener {
                    override fun onClick(view: View, position: Int) {

                        App.prefs.channelId=result.list[position].channelSeq.toString()

                        val fragmentA = BoardMainFragment()
                        val bundle = Bundle()
                        fragmentA.arguments=bundle
                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        transaction.add(R.id.container,fragmentA)
                        transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                        transaction.commit()

                    }
                })


            }

            override fun onFailure(call: Call<channelTeamGetBody>, t: Throwable) {
                Log.e("team_put", "OnFailuer+${t.message}")
            }
        })



        //채널등록
        root.channel_add_button.setOnClickListener {
            var dlg = AlertDialog.Builder(requireContext())
            var dialogView = View.inflate(context, R.layout.dialog_channel_register, null)
            dlg.setView(dialogView)

            var name = dialogView.channel_name.getText()
            dlg.setPositiveButton("확인") { dialogInterface, i ->
                val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
                var retrofit = Retrofit.Builder()
                        .client(okHttpClient)
                        .baseUrl(ChannelService.API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addConverterFactory(ScalarsConverterFactory.create()).build()

                var apiService = retrofit.create(ChannelService::class.java)


                apiService.post_channel(App.prefs.teamSeq!!.toLong(),name.toString()).enqueue(object : Callback<channelGetBody> {
                    override fun onResponse(call: Call<channelGetBody>, response: Response<channelGetBody>) {
                        val result = response.body()
                        Log.e("성공",result.toString())
                        adapter.addItem(name.toString())
                        root.recyclerView_team_menu.adapter = adapter

                    }

                    override fun onFailure(call: Call<channelGetBody>, t: Throwable) {
                        Log.e("team_put", "OnFailuer+${t.message}")
                    }
                })
            }
            dlg.show()

        }



        //메뉴바
        root.menu_button.setOnClickListener {
            main_drawer_layout.openDrawer((GravityCompat.START))
        }

        //팀 관리페이지로 이동
        root.team_update_button.setOnClickListener {
            val fragmentA = TeamUpdateFragment()
            val bundle = Bundle()
            fragmentA.arguments=bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }


        //팀 참여자페이지로 이동
        root.team_member_button.setOnClickListener {
            val fragmentA = TeamMemberFragment()
            val bundle = Bundle()
            fragmentA.arguments=bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }
        return root
    }



}




