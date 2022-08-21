package com.example.avatwin.Fragment.Schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.avatwin.Adapter.Team.teamListAdapter
import com.example.avatwin.Adapter.Team.teamSearchListAdapter
import com.example.avatwin.Adapter.scheduleAdapter
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
import kotlinx.android.synthetic.main.dialog_schedule_detail.*
import kotlinx.android.synthetic.main.dialog_schedule_detail.view.*
import kotlinx.android.synthetic.main.dialog_schedule_list.*
import kotlinx.android.synthetic.main.fragment_schedule_register.*
import kotlinx.android.synthetic.main.fragment_team_register.register_id
import kotlinx.android.synthetic.main.fragment_team_register.register_nickname
import kotlinx.android.synthetic.main.fragment_team_register.register_nickname_check_btn
import kotlinx.android.synthetic.main.fragment_team_register.register_team_list
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ScheduleDetailFragment(var item: scheduleBody): Fragment() {
    var seq = item.scheduleSeq
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.dialog_schedule_detail, container, false)

        root.detail_type.text=item.scheduleType
        root.detail_title.text=item.scheduleName
        root.detail_content.text=item.scheduleDescription
        root.detail_startdate.text = item.scheduleStartDate.toString().substring(0,10)
        root.detail_starttime.text = item.scheduleStartDate.toString().substring(11)
        root.detail_enddate.text = item.scheduleEndDate.toString().substring(0,10)
        root.detail_endtime.text = item.scheduleEndDate.toString().substring(11)
        var participantName:String=""
        if(item.participantName!=null){
        for (i :String in item.participantName){
            participantName = participantName+ " "+i}
        root.detail_participant.text = participantName}

        registerForContextMenu(root.btn_mores)
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


            }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.schdule_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.update -> {

            }
            R.id.delete -> {
                var dlg = AlertDialog.Builder(requireContext())
                var dialogView = View.inflate(context, R.layout.dialog_delete, null)
                dlg.setView(dialogView)
                dlg.setPositiveButton("확인") { dialog, which ->

                    val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
                    var retrofit = Retrofit.Builder()
                        .client(okHttpClient)
                        .baseUrl(ScheduleService.API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addConverterFactory(ScalarsConverterFactory.create()).build()

                    var apiService = retrofit.create(ScheduleService::class.java)


                    apiService.delete_schedule(seq!!).enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            val result = response.body()

                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.e("schedule", "OnFailuer+${t.message}")
                        }
                    })
                }
                dlg.setNegativeButton("취소"){dialog,which ->}
                dlg.show()
            }
        }
        return super.onContextItemSelected(item)
    }
}