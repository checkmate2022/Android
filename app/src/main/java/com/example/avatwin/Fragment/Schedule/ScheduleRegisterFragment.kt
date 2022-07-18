package com.example.avatwin.Fragment.Schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.avatwin.Adapter.Team.teamListAdapter
import com.example.avatwin.Adapter.Team.teamSearchListAdapter
import com.example.avatwin.Auth.App
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.Converter.LocalDateTimeConverter
import com.example.avatwin.DataClass.scheduleGetBody
import com.example.avatwin.DataClass.scheduleReqBody
import com.example.avatwin.DataClass.userGetBody2
import com.example.avatwin.R
import com.example.avatwin.Service.ScheduleService
import com.example.avatwin.Service.UserService
import com.google.gson.GsonBuilder
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
import java.util.*

class ScheduleRegisterFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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
        var start =""
        var end=""
        val items: ArrayList<String> = arrayListOf()
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
                start
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



        //Log.e("Start",start)
        //val startdate= LocalDateTime.parse(start, dateFormatter)

        //enddate= LocalDateTime.parse(end, dateFormatter)}


        //스케쥴 참여자
        val layoutManager = LinearLayoutManager(activity)
        register_team_list.layoutManager = layoutManager
        lateinit var listAdapter: teamListAdapter
        listAdapter = teamListAdapter()



 //스케쥴등록
        val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeConverter())
                .registerTypeAdapter(LocalDateTime::class.java, object: JsonDeserializer<LocalDateTime> {
            override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime {
//			return LocalDateTime.parse(json?.asString,
//				DateTimeFormatter.ofPattern("uuuu-MM-dd[ ]['T']HH:mm:ss.SS[X]").withLocale(Locale.ENGLISH));
                return ZonedDateTime.parse(json?.asString).truncatedTo(ChronoUnit.SECONDS).toLocalDateTime()
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
            Log.e("Date",aLDT.toString())
            val aLDT2 = LocalDateTime.parse(end)
            Log.e("Date",aLDT2.toString())

            var data = scheduleReqBody(register_id.text.toString(), register_description.text.toString(), scheduleType, aLDT, aLDT2, items, App.prefs.teamSeq!!.toLong())
            apiService.post_schedule(data).enqueue(object : Callback<scheduleGetBody> {
                override fun onResponse(call: Call<scheduleGetBody>, response: Response<scheduleGetBody>) {
                    val result = response.body()
                    Log.e("D", result!!.data.scheduleStartdate.toString())
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
                                    register_nickname.setText(register_nickname.getText().toString() + mList.list[position].userId.toString())
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