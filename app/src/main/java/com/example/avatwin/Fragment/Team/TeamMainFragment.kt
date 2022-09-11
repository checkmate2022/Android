package com.example.avatwin.Fragment.Team

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.avatwin.Adapter.Team.teamMenuAdapter
import com.example.avatwin.Adapter.Schedule.scheduleAdapter
import com.example.avatwin.Auth.App
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.Converter.LocalDateTimeConverter
import com.example.avatwin.DataClass.*
import com.example.avatwin.Decorator.OneDayDecorator
import com.example.avatwin.Decorator.TodayDecorator
import com.example.avatwin.Fragment.Board.BoardEntireFragment
import com.example.avatwin.Fragment.Board.BoardMainFragment
import com.example.avatwin.Fragment.ChannelUpdateFragment
import com.example.avatwin.Fragment.Schedule.ScheduleDetailFragment
import com.example.avatwin.Fragment.Schedule.ScheduleRegisterFragment
import com.example.avatwin.R
import com.example.avatwin.Service.ChannelService
import com.example.avatwin.Service.ScheduleService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.android.synthetic.main.dialog_channel_register.view.*
import kotlinx.android.synthetic.main.dialog_schedule_list.*
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
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class TeamMainFragment() : Fragment() {
    //채널 adapter
    val layoutManager1 = LinearLayoutManager(activity)
    val layoutManager2 = LinearLayoutManager(activity)
    lateinit var tnrBottomSheetDialog: BottomSheetDialog
    lateinit var adapter: teamMenuAdapter
    lateinit var scheduleAdapter: scheduleAdapter
    lateinit var calendar: MaterialCalendarView
    lateinit var addDate: CalendarDay

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.menubar_team, container, false)

        var teamMaker=""
        //메뉴바 팀명
        arguments?.let{
            root.channel_teamName.text=it.getString("teamName")
            teamMaker= it.getString("teamMaker").toString()
        }

        if(teamMaker==App.prefs.userId){
            root.team_update_button.isVisible=true
        }


        //일정추가 페이지로 이동
        root.schedule_add_button.setOnClickListener {
            val fragmentA = ScheduleRegisterFragment()
            val bundle = Bundle()
            fragmentA.arguments = bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }

        //메뉴 초기화
        root.recyclerView_team_menu.layoutManager = layoutManager1
        adapter = teamMenuAdapter()
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
                Log.e("성공", result.toString())
                for (i in 0..result.list.size - 1) {
                    adapter.addItem(result.list[i].channelName.toString())
                }
                root.recyclerView_team_menu.adapter = adapter


                adapter.setItemClickListener(object : teamMenuAdapter.ItemClickListener {
                    override fun onClick(view: View, position: Int) {

                        App.prefs.channelId = result.list[position].channelSeq.toString()
                        App.prefs.channelName = result.list[position].channelName.toString()

                        val fragmentA = BoardMainFragment()
                        val bundle = Bundle()
                        fragmentA.arguments = bundle
                        bundle.putString("channelName",result.list[position].channelName)
                        bundle.putLong("channelSeq",result.list[position].channelSeq!!)
                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        transaction.add(R.id.container, fragmentA)
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


                apiService.post_channel(App.prefs.teamSeq!!.toLong(), name.toString()).enqueue(object : Callback<channelGetBody> {
                    override fun onResponse(call: Call<channelGetBody>, response: Response<channelGetBody>) {
                        val result = response.body()
                        Log.e("성공", result.toString())
                        adapter.addItem(name.toString())
                        root.recyclerView_team_menu.adapter = adapter
                        adapter.notifyDataSetChanged()
                        val fragmentA = TeamMainFragment()
                        val bundle = Bundle()
                        fragmentA.arguments = bundle
                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        transaction.add(R.id.container, fragmentA)
                        transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                        transaction.commit()
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

        //채널 전체페이지로 이동
        root.entire_channel.setOnClickListener {
            val fragmentA = BoardEntireFragment()
            val bundle = Bundle()
            fragmentA.arguments = bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }

        //팀 관리페이지로 이동
        //팀장만 보이게

        root.team_update_button.setOnClickListener {
            val fragmentA = TeamUpdateFragment()
            val bundle = Bundle()
            fragmentA.arguments = bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }

        //채널 관리페이지로 이동
        root.channel_update_button.setOnClickListener {
            val fragmentA = ChannelUpdateFragment()
            val bundle = Bundle()
            fragmentA.arguments = bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }

        //팀 참여자페이지로 이동
        root.team_member_button.setOnClickListener {
            val fragmentA = TeamMemberFragment()
            val bundle = Bundle()
            fragmentA.arguments = bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }
        return root
    }
    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getTeamSchedule()

        calendar = view.calendar
        //calendar.setSelectedDate(CalendarDay.today())
        calendar.addDecorator(TodayDecorator())
        //calendar.addDecorator(OneDayDecorator(CalendarDay.today()))

        val tnrBottomSheetView = layoutInflater.inflate(R.layout.dialog_schedule_list, null)

        tnrBottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogCustomTheme) // dialog에 sytle 추가
        tnrBottomSheetDialog.setContentView(tnrBottomSheetView)
        tnrBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        tnrBottomSheetDialog.recycler_schedule.layoutManager = layoutManager2

        calendar.setOnDateChangedListener(object : OnDateSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
                //여기에 가져온 일정 apater추가
                //선택 날짜를 전달,   등록 성공하면 bundle값에 따라
                //https://dpdpwl.tistory.com/3


                tnrBottomSheetDialog.show()
                var str_sub = date.toString().substring(12)
                val r = str_sub.replace("}", "")


                //팀일정중에 startdate이 같은것만 가져온다.
                //숫자가 일의 자리수면 0 을 붙이고 schedulestartdate와 같은것만 adapter에 넣어준다.
                val year = r.substring(0, 4)
                val string = r.substring(5)
                val s = string.split("-")
                val month = s[0].toInt()
                val day = s[1].toInt()
                var rmonth = ""
                var rday = ""

                if ((month+1) / 10 < 1) {
                    rmonth = "0${month + 1}"
                } else {
                    rmonth = "${month + 1 }"
                }

                if (day / 10 < 1) {
                    rday = "0$day"
                } else {
                    rday = day.toString()
                }

                var rselect = ""
                rselect = year + "-" + rmonth + "-" + rday

                tnrBottomSheetDialog.dialog_date.text = rselect
                //날짜에 맞는 어댑터 item 설정


                scheduleAdapter = scheduleAdapter()
                scheduleAdapter.clearItem()
                getTeamDetailSchedule(rselect)
                tnrBottomSheetDialog.btn_write.setOnClickListener {
                    tnrBottomSheetDialog.dismiss()
                    val fragmentA = ScheduleRegisterFragment()
                    val bundle = Bundle()
                    fragmentA.arguments = bundle
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.add(R.id.container, fragmentA)
                    transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                    transaction.commit()
                }

            }

        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTeamSchedule() {

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeConverter())
            .registerTypeAdapter(LocalDateTime::class.java, object : JsonDeserializer<LocalDateTime> {
                override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime {
                    return LocalDateTime.parse(json!!.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                }
            }).create()

        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(ScheduleService.API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(ScheduleService::class.java)


        apiService.get_teamSchedule(App.prefs.teamSeq!!.toLong()).enqueue(object : Callback<scheduleTeamGetBody> {
            override fun onResponse(call: Call<scheduleTeamGetBody>, response: Response<scheduleTeamGetBody>) {
                val result = response.body()
                var scheduleList=ArrayList<scheduleBody>()


                for (i: scheduleBody in result!!.list) {
                    var dateString = i.scheduleStartDate.toString()
                    var year=dateString.substring(0,4)
                    var month=""
                    var day=""
                    //월
                    if(dateString.substring(5,6)=="0"){
                        month = dateString.substring(6,7)
                    }else{
                        month = dateString.substring(5,7)
                    }
                    //일
                    if(dateString.substring(8,9)=="0"){
                        day = dateString.substring(9,10)
                    }else{
                        day = dateString.substring(8,10)
                    }
                    var datE:CalendarDay = CalendarDay.from(year.toInt(),month.toInt()-1,day.toInt())
                    //Log.e("startdate2",datE.toString())
                    calendar.addDecorator(OneDayDecorator(datE))
                }

            }

            override fun onFailure(call: Call<scheduleTeamGetBody>, t: Throwable) {
                Log.e("schedule", "OnFailuer+${t.message}")
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getTeamDetailSchedule(rselect: String) {

        val gson = GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeConverter())
                .registerTypeAdapter(LocalDateTime::class.java, object : JsonDeserializer<LocalDateTime> {
                    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime {
                        return LocalDateTime.parse(json!!.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    }
                }).create()

        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(ScheduleService.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(ScheduleService::class.java)


        apiService.get_teamSchedule(App.prefs.teamSeq!!.toLong()).enqueue(object : Callback<scheduleTeamGetBody> {
            override fun onResponse(call: Call<scheduleTeamGetBody>, response: Response<scheduleTeamGetBody>) {
                val result = response.body()
                var scheduleList=ArrayList<scheduleBody>()
                for (i: scheduleBody in result!!.list) {
                    if (i.scheduleStartDate.toString().substring(0, 10) == rselect) {
                        //다른 정보를 가져온다. 확인할것
                        scheduleList.add(i)
                        scheduleAdapter.addItem(i)
                    }
                }

                tnrBottomSheetDialog.recycler_schedule.adapter = scheduleAdapter
                scheduleAdapter.setItemClickListener(object : scheduleAdapter.ItemClickListener {
                    override fun onClick(view: View, position: Int) {
                  /*
                        //tnrBottomSheetDialog2 = BottomSheetDialog(requireContext(), R.style.DialogCustomTheme)
                        tnrBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                        val tnrBottomSheetView = layoutInflater.inflate(R.layout.dialog_schedule_detail, null)
                        //왜 안될까
                        tnrBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                        tnrBottomSheetDialog.setContentView(tnrBottomSheetView)

                        registerForContextMenu(tnrBottomSheetDialog.btn_mores)
                        tnrBottomSheetDialog.show()
                        tnrBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
*/
                        tnrBottomSheetDialog.dismiss()
                        val fragmentA = ScheduleDetailFragment( scheduleList[position])
                        Log.e("scheduleList",scheduleList[position].toString())
                        val bundle = Bundle()
                        fragmentA.arguments = bundle
                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        transaction.add(R.id.container, fragmentA)
                        transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                        transaction.commit()

                    }
                })

            }

            override fun onFailure(call: Call<scheduleTeamGetBody>, t: Throwable) {
                Log.e("schedule", "OnFailuer+${t.message}")
            }
        })
    }




}




