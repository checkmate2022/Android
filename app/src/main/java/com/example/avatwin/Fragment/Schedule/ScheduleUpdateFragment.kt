package com.example.avatwin.Fragment.Schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.avatwin.Adapter.Schedule.scheduleUpdateMemberAdapter
import com.example.avatwin.Adapter.Team.teamListAdapter
import com.example.avatwin.Adapter.Team.teamSearchListAdapter
import com.example.avatwin.Adapter.Team.teamUpdateMemberAdapter
import com.example.avatwin.Auth.App
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.Converter.LocalDateTimeConverter
import com.example.avatwin.DataClass.*
import com.example.avatwin.Fragment.Team.TeamMainFragment
import com.example.avatwin.R
import com.example.avatwin.Service.ScheduleService
import com.example.avatwin.Service.UserService
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.dialog_member_search.view.*
import kotlinx.android.synthetic.main.dialog_schedule_detail.view.*
import kotlinx.android.synthetic.main.fragment_schedule_register.*
import kotlinx.android.synthetic.main.fragment_schedule_register.register_description
import kotlinx.android.synthetic.main.fragment_schedule_register.register_end_day
import kotlinx.android.synthetic.main.fragment_schedule_register.register_end_time
import kotlinx.android.synthetic.main.fragment_schedule_register.register_schedule_button
import kotlinx.android.synthetic.main.fragment_schedule_register.register_schedule_type
import kotlinx.android.synthetic.main.fragment_schedule_register.register_start_day
import kotlinx.android.synthetic.main.fragment_schedule_register.register_start_time
import kotlinx.android.synthetic.main.fragment_schedule_register.spinner
import kotlinx.android.synthetic.main.fragment_schedule_update.*
import kotlinx.android.synthetic.main.fragment_schedule_update.view.*
import kotlinx.android.synthetic.main.fragment_schedule_update.view.register_id
import kotlinx.android.synthetic.main.fragment_schedule_update.view.register_team_list
import kotlinx.android.synthetic.main.fragment_team_register.register_id
import kotlinx.android.synthetic.main.fragment_team_register.register_nickname
import kotlinx.android.synthetic.main.fragment_team_register.register_nickname_check_btn
import kotlinx.android.synthetic.main.fragment_team_register.register_team_list
import kotlinx.android.synthetic.main.fragment_team_update.view.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ScheduleUpdateFragment(var item: scheduleBody): Fragment() {
    init{ instance = this }

    companion object{
        private var instance: ScheduleUpdateFragment? = null
        fun getInstance(): ScheduleUpdateFragment?
        { return instance  }}
    val items: ArrayList<String> = arrayListOf()
    val layoutManager = LinearLayoutManager(activity)
    lateinit var listAdapter: scheduleUpdateMemberAdapter
    var startDateString = ""
    var startTimeString = ""
    var endDateString = ""
    var endTimeString = ""
    var start =""
    var end=""

    var notificationTime=0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_schedule_update, container, false)

        //기존 정보 가져오기

        root.register_id.setText(item.scheduleName)
        root.register_description.setText(item.scheduleDescription)

        //일정타입
        var typeString=""
        if(item.scheduleType=="basic"){
            root.basic.isChecked=true
        }else if(item.scheduleType=="conference"){
            root.conference.isChecked=true
        }
        //알람

        //참여자
        root.register_team_list.layoutManager = layoutManager
        listAdapter = scheduleUpdateMemberAdapter()
        val len: Int = item.participants.size
        for(i in 0..len-1){
            if(item.participants[i]!=App.prefs.userId){
                listAdapter.addItem(item.participants[i])
                //items.add(result.list[i].userId.toString()
                root.update_memberList.setText(update_memberList.getText().toString()+" "+item.participants[i])
            }
        }
        root.register_team_list.adapter = listAdapter
        //시작날짜
        root.register_start_day.text = item.scheduleStartDate.toString().substring(0,10)
        //시작시간
        root.register_start_time.text = item.scheduleStartDate.toString().substring(11,16)

        //끝날짜
        root.register_end_day.text = item.scheduleEndDate.toString().substring(0,10)
       //끝시간
        root.register_end_time.text = item.scheduleEndDate.toString().substring(11,16)
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items: ArrayList<String> = arrayListOf()

        //알람 스피너 구현
        spinner.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.list, android.R.layout.simple_spinner_item)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0){
                    notificationTime=0
                }else if(position == 1){
                    notificationTime=10
                }else if(position == 2){
                    notificationTime=30
                }else{
                    notificationTime=60
                } }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            } }

        //시작 날짜
        register_start_day.setOnClickListener {

            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                startDateString = "${year}-${month + 1}-${dayOfMonth}"

                register_start_day.text = startDateString
                //val strDatewithTime = "2015-08-04T10:11:30"
               // val aLDT = LocalDateTime.parse(strDatewithTime)
               //Log.e("Date",aLDT.toString())
            }
            DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()

        }

        //시작 시간
        register_start_time.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                startTimeString = "${hourOfDay}:${minute}"
                register_start_time.text = startTimeString
                start = startDateString+"T"+startTimeString+":01"
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        //종료날짜
        register_end_day.setOnClickListener {

            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                endDateString = "${year}-${month + 1}-${dayOfMonth}"
                register_end_day.text = endDateString
            }
            DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        //종료 시간
        register_end_time.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                endTimeString = "${hourOfDay}:${minute}"
                register_end_time.text = endTimeString
                end = endDateString+"T"+endTimeString+":01"
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        //일정종류
        var scheduleType = ""
        register_schedule_type.setOnCheckedChangeListener{ group, checkedId ->
            when(checkedId){
                R.id.basic -> scheduleType = "BASIC"

                R.id.conference -> scheduleType = "CONFERENCE"

            }
        }

        //스케쥴 참여자
        val layoutManager = LinearLayoutManager(activity)
        register_team_list.layoutManager = layoutManager
        lateinit var listAdapter: teamListAdapter
        listAdapter = teamListAdapter()


        val gson = GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeConverter())
                .registerTypeAdapter(LocalDateTime::class.java, object: JsonDeserializer<LocalDateTime> {
            override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime {
                return LocalDateTime.parse(json!!.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            }
        }).create()

        //스케쥴등록
        register_schedule_button.setOnClickListener {
            val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
            var retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(ScheduleService.API_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson)).build()

            var apiService = retrofit.create(ScheduleService::class.java)


            val aLDT = LocalDateTime.parse(start)
            Log.e("Date",aLDT.toString())
            val aLDT2 = LocalDateTime.parse(end)
            Log.e("Date",aLDT2.toString())

            var data = scheduleReqBody(register_id.text.toString(), register_description.text.toString(), scheduleType, aLDT, aLDT2, items, App.prefs.teamSeq!!.toLong(),notificationTime)
            apiService.post_schedule(data).enqueue(object : Callback<scheduleGetBody> {
                override fun onResponse(call: Call<scheduleGetBody>, response: Response<scheduleGetBody>) {
                    val result = response.body()
                   // Log.e("D", result!!.data.scheduleStartdate.toString())

                    val fragmentA = TeamMainFragment()
                    val bundle = Bundle()
                    fragmentA.arguments=bundle
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.add(R.id.container,fragmentA)
                    transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                    transaction.commit()
                }
                override fun onFailure(call: Call<scheduleGetBody>, t: Throwable) {
                    Log.e("schedule", "OnFailuer+${t.message}")
                } })
        }

    //참여자
        register_nickname_check_btn.setOnClickListener {

            var dlg = AlertDialog.Builder(requireContext())
            var dialogView = View.inflate(context, R.layout.dialog_member_search, null)
            dlg.setView(dialogView)
            dlg.show()
            var seachtext = dialogView.search_edittext.getText()

            val layoutManager1 = LinearLayoutManager(activity)
            dialogView.recyclerView_search.layoutManager = layoutManager1
            lateinit var adapter: teamSearchListAdapter
            adapter = teamSearchListAdapter()

            dialogView.search_btn.setOnClickListener {

                val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
                var retrofit = Retrofit.Builder()
                        .client(okHttpClient)
                        .baseUrl(UserService.API_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build()
                var apiService = retrofit.create(UserService::class.java)
                var tests = apiService.get_user_search(seachtext.toString())
                tests.enqueue(object : Callback<userGetBody2> {
                    override fun onResponse(call: Call<userGetBody2>, response: Response<userGetBody2>) {
                        if (response.isSuccessful) {
                            var mList = response.body()!!
                            //adapter.addItem(mList.data[0])
                            Log.e("teamDialog", mList.toString())

                            for (i: joinGetBody in mList.list) {
                                if (i.userId.toString() != App.prefs.userId) {
                                    adapter.addItem(i)
                                }
                            }
                            dialogView.recyclerView_search.adapter = adapter


                            adapter.setItemClickListener(object : teamSearchListAdapter.ItemClickListener {
                                override fun onClick(view: View, position: Int) {
                                    Log.e("ddd", "Ss")
                                    update_memberList.setText(update_memberList.getText().toString() + mList.list[position].userId.toString())
                                    items.add(mList.list[position].userId.toString())
                                    listAdapter.addItem(mList.list[position].userId.toString())
                                    register_team_list.adapter = listAdapter

                                } }) } }
                    override fun onFailure(call: Call<userGetBody2>, t: Throwable) {
                        Log.e("teamDialog", "OnFailuer+${t.message}")
                    } }) }
        } }

    fun deleteMember(position:Int){

        register_team_list.adapter!!.notifyDataSetChanged()
        register_nickname.setText("")
        for(i:String in items){
            update_memberList.setText(update_memberList.getText().toString()+" " +i)
        }
    }
}