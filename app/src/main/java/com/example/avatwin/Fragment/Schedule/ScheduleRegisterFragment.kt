package com.example.avatwin.Fragment.Schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.avatwin.Adapter.teamListAdapter
import com.example.avatwin.Adapter.teamSearchListAdapter
import com.example.avatwin.Auth.App
import com.example.avatwin.R
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.DataClass.*
import com.example.avatwin.Fragment.HomeFragment
import com.example.avatwin.Fragment.Team.TeamMainFragment
import com.example.avatwin.Service.ScheduleService
import com.example.avatwin.Service.TeamService
import com.example.avatwin.Service.UserService
import kotlinx.android.synthetic.main.dialog_channel_register.view.*
import kotlinx.android.synthetic.main.dialog_member_search.view.*
import kotlinx.android.synthetic.main.fragment_avatar_register.*
import kotlinx.android.synthetic.main.fragment_schedule_register.*
import kotlinx.android.synthetic.main.fragment_team_register.*
import kotlinx.android.synthetic.main.fragment_team_register.register_id
import kotlinx.android.synthetic.main.fragment_team_register.register_nickname
import kotlinx.android.synthetic.main.fragment_team_register.register_nickname_check_btn
import kotlinx.android.synthetic.main.fragment_team_register.register_pwd
import kotlinx.android.synthetic.main.fragment_team_register.register_team_button
import kotlinx.android.synthetic.main.fragment_team_register.register_team_list
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ScheduleRegisterFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_schedule_register, container, false)



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var startDateString = ""
        var startTimeString = ""
        var endDateString = ""
        var endTimeString = ""
        var start =""
        var end=""
        val items: ArrayList<String> = arrayListOf()
        //?????? ??????
        register_start_day.setOnClickListener {

            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                startDateString = "${year}-${month + 1}-${dayOfMonth}"
                register_start_day.text = startDateString
            }
            DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()

        }

        //?????? ??????
        register_start_time.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                startTimeString = "${hourOfDay}:${minute}"
                register_start_time.text = startTimeString
                start = startDateString+" "+startTimeString+":00"
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),true).show()
        }

        //????????????
        register_end_day.setOnClickListener {

            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                endDateString = "${year}-${month + 1}-${dayOfMonth}"
                register_end_day.text = endDateString
            }
            DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        //?????? ??????
        register_end_time.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                endTimeString = "${hourOfDay}:${minute}"
                register_end_time.text = endTimeString
                end = endDateString+" "+endTimeString+":00"
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),true).show()
        }

        //????????????
        var scheduleType = ""
        register_schedule_type.setOnCheckedChangeListener{ group, checkedId ->
            when(checkedId){
                R.id.basic -> scheduleType = "BASIC"

                R.id.conference -> scheduleType = "CONFERENCE"

            }
        }

        val dateFormatter = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        } else {
            TODO("VERSION.SDK_INT < O")
        }


        //Log.e("Start",start)
        //val startdate= LocalDateTime.parse(start, dateFormatter)

        //enddate= LocalDateTime.parse(end, dateFormatter)}


        //????????? ?????????
        val layoutManager = LinearLayoutManager(activity)
        register_team_list.layoutManager = layoutManager
        lateinit var listAdapter: teamListAdapter
        listAdapter = teamListAdapter()



 //???????????????

        register_schedule_button.setOnClickListener {
            val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
            var retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(ScheduleService.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create()).build()

            var apiService = retrofit.create(ScheduleService::class.java)

            //LocalDateTime.parse(start, dateFormatter)
            var data = scheduleReqBody(register_id.text.toString(),register_description.text.toString(),scheduleType,start,end,items, App.prefs.teamSeq!!.toLong())
            apiService.post_schedule(data).enqueue(object : Callback<scheduleGetBody> {
                override fun onResponse(call: Call<scheduleGetBody>, response: Response<scheduleGetBody>) {
                    val result = response.body()
                    Log.e("D",result.toString())
                    /*
                    val fragmentA = TeamMainFragment()
                    val bundle = Bundle()
                    fragmentA.arguments=bundle
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.add(R.id.container,fragmentA)
                    transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                    transaction.commit()*/

                }

                override fun onFailure(call: Call<scheduleGetBody>, t: Throwable) {
                    Log.e("schedule", "OnFailuer+${t.message}")
                }
            })

        }



    //?????????
        register_nickname_check_btn.setOnClickListener {


            var dlg = AlertDialog.Builder(requireContext())
            var dialogView = View.inflate(context, R.layout.dialog_member_search, null)
            dlg.setView(dialogView)
            dlg.show()
            var seachtext = dialogView.search_edittext.getText()


            val layoutManager1 = LinearLayoutManager(activity)
            dialogView.recyclerView_search.layoutManager = layoutManager1
            lateinit var adapter: teamSearchListAdapter


            dialogView.search_btn.setOnClickListener {

                Log.e("Dd", "ss")
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

                            adapter = teamSearchListAdapter(mList.list)
                            dialogView.recyclerView_search.adapter = adapter


                            adapter.setItemClickListener(object : teamSearchListAdapter.ItemClickListener {
                                override fun onClick(view: View, position: Int) {
                                    Log.e("ddd", "Ss")
                                    register_nickname.setText(register_nickname.getText().toString() +mList.list[position].userId.toString())
                                    items.add(mList.list[position].userId.toString())
                                    listAdapter.addItem(mList.list[position].userId.toString())
                                    register_team_list.adapter = listAdapter

                                }
                            })
                        }


                    }

                    override fun onFailure(call: Call<userGetBody2>, t: Throwable) {
                        Log.e("teamDialog", "OnFailuer+${t.message}")
                    }
                })
            }


        }














    }

}