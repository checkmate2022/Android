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
import com.example.avatwin.Adapter.Team.teamSearchListAdapter
import com.example.avatwin.Adapter.scheduleMemberAdapter
import com.example.avatwin.Auth.App
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.Converter.LocalDateTimeConverter
import com.example.avatwin.DataClass.joinGetBody
import com.example.avatwin.DataClass.scheduleGetBody
import com.example.avatwin.DataClass.scheduleReqBody
import com.example.avatwin.DataClass.userGetBody2
import com.example.avatwin.Fragment.Team.TeamMainFragment
import com.example.avatwin.R
import com.example.avatwin.Service.ScheduleService
import com.example.avatwin.Service.UserService
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.dialog_member_search.view.*
import kotlinx.android.synthetic.main.fragment_schedule_register.*
import kotlinx.android.synthetic.main.fragment_team_register.register_id
import kotlinx.android.synthetic.main.fragment_team_register.register_nickname
import kotlinx.android.synthetic.main.fragment_team_register.register_nickname_check_btn
import kotlinx.android.synthetic.main.fragment_team_register.register_team_list
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

class ScheduleRegisterFragment : Fragment() {
    init{ instance = this }

    companion object{
        private var instance: ScheduleRegisterFragment? = null
        fun getInstance(): ScheduleRegisterFragment?
        { return instance  }}

    val items: ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.fragment_schedule_register, container, false)
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var startDateString = ""
        var startTimeString = ""
        var endDateString = ""
        var endTimeString = ""
        var start = ""
        var end = ""
        var smonth = ""
        var sday = ""
        var emonth = ""
        var eday = ""
        var shour = ""
        var smin = ""
        var ehour = ""
        var emin = ""

        var dateforMain = ""

        //알람 스피너 구현
        //val itemList = listOf("10분전", "30분전", "1시간전")
        //val adapter = ArrayAdapter(this, itemList)
        spinner.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.list, android.R.layout.simple_spinner_item)
        //spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
               // if (position != 0)
                    //Toast.makeText(this@MainActivity, itemList[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        //시작 날짜
        register_start_day.setOnClickListener {

            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, day ->

                if ((month + 1) / 10 < 1) {
                    smonth = "0${month + 1}"
                } else {
                    smonth = "${month + 1}"
                }

                if (day / 10 < 1) {
                    sday = "0$day"
                } else {
                    sday = day.toString()
                }

                dateforMain = "${year}-${month + 1}-${day}"
                startDateString = "${year}-${smonth}-${sday}"
                register_start_day.text = startDateString

            }
            DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            ).show()

        }

        //시작 시간
        register_start_time.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hour, minute ->

                if ((hour) / 10 < 1) {
                    shour = "0${hour}"
                } else {
                    shour = "${hour}"
                }

                if ((minute) / 10 < 1) {
                    smin = "0${minute}"
                } else {
                    smin = "${minute}"
                }

                startTimeString = "${shour}:${smin}"
                register_start_time.text = startTimeString
                start = startDateString + "T" + startTimeString + ":00"
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        //종료날짜
        register_end_day.setOnClickListener {

            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, day ->

                if ((month + 1) / 10 < 1) {
                    emonth = "0${month + 1}"
                } else {
                    emonth = "${month + 1}"
                }

                if (day / 10 < 1) {
                    eday = "0$day"
                } else {
                    eday = day.toString()
                }

                endDateString = "${year}-${emonth}-${eday}"
                register_end_day.text = endDateString
            }
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        //종료 시간
        register_end_time.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hour, minute ->

                if ((hour) / 10 < 1) {
                    ehour = "0${hour}"
                } else {
                    ehour = "${hour}"
                }

                if ((minute) / 10 < 1) {
                    emin = "0${minute}"
                } else {
                    emin = "${minute}"
                }

                endTimeString = "${ehour}:${emin}"
                register_end_time.text = endTimeString
                end = endDateString + "T" + endTimeString + ":00"
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        //일정종류
        var scheduleType = ""
        register_schedule_type.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.basic -> scheduleType = "BASIC"

                R.id.conference -> scheduleType = "CONFERENCE"

            }
        }


        //스케쥴 참여자
        val layoutManager = LinearLayoutManager(activity)
        register_team_list.layoutManager = layoutManager
        //lateinit var listAdapter: teamListAdapter
        //listAdapter = teamListAdapter()
        lateinit var listAdapter: scheduleMemberAdapter
        listAdapter = scheduleMemberAdapter()

        //스케쥴등록
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeConverter())
            .registerTypeAdapter(
                LocalDateTime::class.java,
                object : JsonDeserializer<LocalDateTime> {
                    override fun deserialize(
                        json: JsonElement?,
                        typeOfT: Type?,
                        context: JsonDeserializationContext?
                    ): LocalDateTime {
                        return LocalDateTime.parse(
                            json!!.asString,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        )
                    }
                }).create()

        register_schedule_button.setOnClickListener {
            val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
            var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(ScheduleService.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)).build()

            var apiService = retrofit.create(ScheduleService::class.java)


            val aLDT = LocalDateTime.parse(start)
            //Log.e("Date",aLDT.toString())
            val aLDT2 = LocalDateTime.parse(end)
            //Log.e("Date",aLDT2.toString())

            var data = scheduleReqBody(
                register_id.text.toString(),
                register_description.text.toString(),
                scheduleType,
                aLDT,
                aLDT2,
                items,
                App.prefs.teamSeq!!.toLong()
            )
            apiService.post_schedule(data).enqueue(object : Callback<scheduleGetBody> {
                override fun onResponse(
                    call: Call<scheduleGetBody>,
                    response: Response<scheduleGetBody>
                ) {
                    val result = response.body()
                    //Log.e("D", result!!.data.scheduleStartdate.toString())

                    val fragmentA = TeamMainFragment()
                    val bundle = Bundle()
                    bundle.putString("startDate", dateforMain)
                    fragmentA.arguments = bundle
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.add(R.id.container, fragmentA)
                    transaction.replace(R.id.container, fragmentA.apply { arguments = bundle })
                        .addToBackStack(null)
                    transaction.commit()

                }

                override fun onFailure(call: Call<scheduleGetBody>, t: Throwable) {
                    Log.e("schedule", "OnFailuer+${t.message}")
                }
            })

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

                Log.e("Dd", "ss")
                val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
                var retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(UserService.API_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build()
                var apiService = retrofit.create(UserService::class.java)
                var tests = apiService.get_user_search(seachtext.toString())
                tests.enqueue(object : Callback<userGetBody2> {
                    override fun onResponse(
                        call: Call<userGetBody2>,
                        response: Response<userGetBody2>
                    ) {
                        if (response.isSuccessful) {
                            var mList = response.body()!!
                            //adapter.addItem(mList.data[0])
                            // Log.e("teamDialog", mList.toString())

                            for (i: joinGetBody in mList.list) {
                                if (i.userId.toString() != App.prefs.userId) {
                                    adapter.addItem(i)
                                }
                            }
                            dialogView.recyclerView_search.adapter = adapter


                            adapter.setItemClickListener(object :
                                teamSearchListAdapter.ItemClickListener {
                                override fun onClick(view: View, position: Int) {
                                    //Log.e("ddd", "Ss")
                                    register_nickname.setText(register_nickname.getText().toString()+" "+ mList.list[position].userId.toString()
                                    )
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
    fun deleteMember(position:Int){

        register_team_list.adapter!!.notifyDataSetChanged()
        items.remove(items[position])
        register_nickname.setText("")
        for(i:String in items){
            register_nickname.setText(register_nickname.getText().toString()+" " +i)
        }
    }
}