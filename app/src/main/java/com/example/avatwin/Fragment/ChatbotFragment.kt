package com.example.avatwin.Fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.avatwin.Adapter.Chatbot.chatbotAdapter
import com.example.avatwin.Adapter.Schedule.scheduleAdapter
import com.example.avatwin.Auth.App
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.Converter.LocalDateTimeConverter
import com.example.avatwin.DataClass.*
import com.example.avatwin.Decorator.OneDayDecorator
import com.example.avatwin.Decorator.TodayDecorator
import com.example.avatwin.Fragment.Schedule.ScheduleDetailFragment
import com.example.avatwin.Fragment.Schedule.ScheduleRegisterFragment
import com.example.avatwin.R
import com.example.avatwin.Service.ScheduleService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.dialogflow.v2.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.protobuf.Struct
import com.google.protobuf.Value
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.android.synthetic.main.dialog_chatbot_schedule.*
import kotlinx.android.synthetic.main.dialog_chatbot_schedule.view.*
import kotlinx.android.synthetic.main.dialog_member_search.view.*
import kotlinx.android.synthetic.main.dialog_schedule_date.view.*
import kotlinx.android.synthetic.main.dialog_schedule_list.*
import kotlinx.android.synthetic.main.fragment_chatbot.*
import kotlinx.android.synthetic.main.fragment_chatbot.view.*
import kotlinx.android.synthetic.main.fragment_schedule_register.*
import kotlinx.android.synthetic.main.fragment_team_main.view.*
import kotlinx.android.synthetic.main.fragment_team_main.view.calendar
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
import java.util.*


class ChatbotFragment:Fragment() {

    init{ instance = this }

    companion object{
        private var instance: ChatbotFragment? = null
        fun getInstance(): ChatbotFragment?
        { return instance  }}

    private var messageList: ArrayList<Message> = ArrayList()

    //dialogFlow
    private var sessionsClient: SessionsClient? = null
    private var sessionName: SessionName? = null
    private val uuid = UUID.randomUUID().toString()
    private val TAG = "DIALOGFLOW"
    private lateinit var chatAdapter: chatbotAdapter
    var teamSeq: Long=0
    var CHECK_TYPE =false
    var CHECK_TITLE =false
    var CHECK_DATE=false
    var CHECK_NOTIFICATION=false
    var CHECK_NOTIFICATION_TIME=false
    var war=false
    var startDateString = ""
    var startTimeString = ""
    var endDateString = ""
    var endTimeString = ""
    var start = ""
    var end = ""
    var scheduleType=""
    var scheduleTitle=""

    //알람설정 변수선언
    lateinit var scheduleAdapter: scheduleAdapter
    lateinit var calendar: MaterialCalendarView
    var scheduleList = ArrayList<scheduleBody>()
    var scheduleSeq =0
    var notificationTime=""
    var notificationDate=""
    var dlgN = AlertDialog.Builder(requireContext())
    var dialogViewN = View.inflate(context, R.layout.dialog_chatbot_schedule, null)

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_chatbot, container, false)
        chatAdapter = chatbotAdapter(requireActivity(), messageList)

        root.chatView.adapter = chatAdapter

        //onclick listener to update the list and call dialogflow
        root.btnSend.setOnClickListener {
            val message: String = root.editMessage.text.toString()
            if (message.isNotEmpty()) {
                if(CHECK_TYPE){
                    Log.e("bot","타입")
                    addMessageToList(message, false)
                    sendMessageToBot(message)
                    CHECK_TYPE=false
                    CHECK_TITLE=true}
                else if(CHECK_TITLE) {
                    Log.e("bot","일정명")
                    addMessageToList(message, false)
                    sendMessageToBot("일정명 "+message)
                    scheduleTitle=message
                    CHECK_TITLE=false
                }else if(CHECK_DATE) {
                    Log.e("bot","총일정")
                    addMessageToList(message, false)
                    //Log.e("bot","o")
                    //sendFianlMessageToBot("총일정 "+scheduleType)
                    sendRegisterMessageToBot("총일정 "+start+" "+end+" "+scheduleType)
                    CHECK_DATE=false
                }else if(CHECK_NOTIFICATION) {
                    Log.e("bot","알람")
                    addMessageToList(message, false)
                    notificationDate = message
                    sendMessageToBot("지정일 "+message)
                    CHECK_NOTIFICATION=false
                    Log.e("bot",CHECK_NOTIFICATION.toString())
                }else if(CHECK_NOTIFICATION_TIME) {
                    Log.e("bot","알람시간")
                    addMessageToList(message, false)
                    CHECK_NOTIFICATION_TIME=false
                    sendNotificationMessageToBot("알람 "+message+"분전")
                }else {
                    //Log.e("Check_Date",CHECK_DATE.toString())
                    //Log.e("bot","그냥보내기")
                addMessageToList(message, false)
                sendMessageToBot(message)}
            } else {
                //Toast.makeText(this@MainActivity, "Please enter text!", Toast.LENGTH_SHORT).show()
            }
        }

        //initialize bot config
        setUpBot()

        return root
    }

    //ui 에 메시지 업로드
    @SuppressLint("NotifyDataSetChanged")
    private fun addMessageToList(message: String, isReceived: Boolean) {
        messageList.add(Message(message, isReceived))
        editMessage.setText("")
        chatAdapter.notifyDataSetChanged()
        chatView.layoutManager?.scrollToPosition(messageList.size - 1)
    }

    //dialogflow 연동
    private fun setUpBot() {
        try {
            val stream = this.resources.openRawResource(R.raw.credential)
            val credentials: GoogleCredentials = GoogleCredentials.fromStream(stream)
                .createScoped("https://www.googleapis.com/auth/cloud-platform")
            val projectId: String = (credentials as ServiceAccountCredentials).projectId
            val settingsBuilder: SessionsSettings.Builder = SessionsSettings.newBuilder()
            val sessionsSettings: SessionsSettings = settingsBuilder.setCredentialsProvider(
                FixedCredentialsProvider.create(credentials)
            ).build()
            sessionsClient = SessionsClient.create(sessionsSettings)
            sessionName = SessionName.of(projectId, uuid)
            Log.e(TAG, "projectId : $projectId")
        } catch (e: Exception) {
            Log.e(TAG, "setUpBot: " + e.message)
        }
    }
    //메시지 작성 함수
    private fun sendMessageToBot(message: String) {

        val queryInput = QueryInput.newBuilder()
            .setText(TextInput.newBuilder().setText(message).setLanguageCode("ko"))
            .build()

        val s = QueryParameters.newBuilder().setPayload(
            Struct.newBuilder().putFields(
                "userId",
                Value.newBuilder().setStringValue(App.prefs.userId).build()
            ).build()
        ).build()


        GlobalScope.launch {
            sendMessageInBg(queryInput,s)
        }
    }
    private fun sendRegisterMessageToBot(message: String) {

        val queryInput = QueryInput.newBuilder()
            .setText(TextInput.newBuilder().setText(message).setLanguageCode("ko"))
            .build()

        val s = QueryParameters.newBuilder().setPayload(
            Struct.newBuilder().putFields(
                "userId",
                Value.newBuilder().setStringValue(App.prefs.userId).build()
            ).putFields(
                "teamSeq",
                Value.newBuilder().setStringValue(teamSeq.toString()).build()
            ).putFields(
                "scheduleTitle",
                Value.newBuilder().setStringValue(scheduleTitle).build()
            ).putFields(
                    "scheduleStartDate",
                    Value.newBuilder().setStringValue(startDateString + "T" + startTimeString).build())
                .putFields(
                    "scheduleEndDate",
                    Value.newBuilder().setStringValue(endDateString + "T" + endTimeString).build()
                ).putFields(
                    "scheduleType",
                    Value.newBuilder().setStringValue(scheduleType).build()
                )
                .build()
        ).build()


        GlobalScope.launch {
            sendMessageInBg(queryInput,s)
        }
    }
    private fun sendNotificationMessageToBot(message: String) {

        val queryInput = QueryInput.newBuilder()
            .setText(TextInput.newBuilder().setText(message).setLanguageCode("ko"))
            .build()

        val s = QueryParameters.newBuilder().setPayload(
            Struct.newBuilder().putFields(
                "userId",
                Value.newBuilder().setStringValue(App.prefs.userId).build()
            ).putFields(
                "notificationDate",
                Value.newBuilder().setStringValue(notificationDate).build()
            ).putFields(
                "notificationTime",
                Value.newBuilder().setStringValue(notificationTime).build()
            ).build()
        ).build()


        GlobalScope.launch {
            sendMessageInBg(queryInput,s)
        }
    }

    //전송 버튼을 누르면 dialogflow에 메시지 전송
    private suspend fun sendMessageInBg(queryInput: QueryInput,s:QueryParameters) {
      withContext(Default) {
            try {
               // val queryParameters = QueryParameters.newBuilder().setPayload(Struct.parseFrom(App.prefs.userId!!.toByteArray())).build()

                val detectIntentRequest = DetectIntentRequest.newBuilder()
                    .setSession(sessionName.toString())
                    .setQueryInput(queryInput)
                    .setQueryParams(s)
                    .build()
                val result = sessionsClient?.detectIntent(detectIntentRequest)
                if (result != null) {
                    requireActivity().runOnUiThread {
                        updateUI(result)
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.e(TAG, "doInBackground: " + e.message)
                e.printStackTrace()
            }
        }
    }

    //ui 업데이트 함수
    private fun updateUI(response: DetectIntentResponse) {
        val botReply: String = response.queryResult.fulfillmentText
        if (botReply.isNotEmpty()) {

            addMessageToList(botReply, true)
        } else {
            //Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    fun clickTeamName(team: teamaBody){
        editMessage.setText("팀이름 "+team.teamName)
        teamSeq=team.teamSeq!!
    }

    fun clickScheduleType(type: String){
        editMessage.setText(type)
        scheduleType=type
        CHECK_TYPE=true
       // Log.e("team",teamSeq.toString())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun clickScheduleDate(){
        var smonth = ""
        var sday = ""
        var emonth = ""
        var eday = ""
        var shour = ""
        var smin = ""
        var ehour = ""
        var emin = ""
        var dlg = AlertDialog.Builder(requireContext())
        var dialogView = View.inflate(context, R.layout.dialog_schedule_date, null)
        dlg.setView(dialogView)

        dialogView.start_date.setOnDateChangedListener { datePicker, year, month, day ->
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

            startDateString = "${year}-${smonth}-${sday}"

        }
        dialogView.start_time.setOnTimeChangedListener { timePicker, hour, minute ->
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

            startTimeString = "${shour}:${smin}:00"
            dialogView.start_txt.text = startDateString+" "+startTimeString

        }
        dialogView.end_date.setOnDateChangedListener { datePicker, year, month, day ->
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

        }
        dialogView.end_time.setOnTimeChangedListener { timePicker, hour, minute ->
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

            endTimeString = "${ehour}:${emin}:00"
            dialogView.end_txt.text = endDateString+" "+endTimeString
        }
        dlg.setPositiveButton("확인") { dialog, which ->
            start=" 시작 : "+startDateString+" "+startTimeString
            end=" 끝 : "+endDateString+" "+endTimeString
            editMessage.setText(start+" "+end)
            CHECK_DATE=true
            Log.e("Check_Date",CHECK_DATE.toString())
        }
        dlg.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun clickNotification(){

      //날짜를 chatbot에 보내고 seq저장해야함
        //diaglof 안에날짜 클릭하면 밑에 스케쥴이 뜨고 그 스케쥴클릭시 글자바뀌고 채팅창에 입력, seq저장

        //diag가져오기
      //var dlgN = AlertDialog.Builder(requireContext())

        dlgN.setView(dialogViewN)

        getTeamSchedule()

        calendar = requireView().calendar
        calendar.addDecorator(TodayDecorator())


        calendar.setOnDateChangedListener(object : OnDateSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
                //여기에 가져온 일정 apater추가
                //선택 날짜를 전달,   등록 성공하면 bundle값에 따라
                //https://dpdpwl.tistory.com/3

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

                //tnrBottomSheetDialog.dialog_date.text = rselect
                //날짜에 맞는 어댑터 item 설정
                scheduleAdapter = scheduleAdapter()
                scheduleAdapter.clearItem()
                getTeamDetailSchedule(rselect)


            }

        })
        dlgN.setPositiveButton("확인") { dialog, which ->
            CHECK_NOTIFICATION=true
            Log.e("Check_Date",CHECK_DATE.toString())
        }
        dlgN.show()


    }


    //기본 스케쥴 가져오기
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTeamSchedule() {

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

        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(ScheduleService.API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(ScheduleService::class.java)


        apiService.get_chatbotSchedule()
            .enqueue(object : Callback<scheduleTeamGetBody> {
                override fun onResponse(
                    call: Call<scheduleTeamGetBody>, response: Response<scheduleTeamGetBody>) {
                    val result = response.body()

                    var scheduleList = ArrayList<scheduleBody>()
                  //  Log.e("ScheduleList",result!!.toString())

                    for (i: scheduleBody in result!!.list) {
                        var dateString = i.scheduleStartDate.toString()
                        var year = dateString.substring(0, 4)
                        var month = ""
                        var day = ""
                        //월
                        if (dateString.substring(5, 6) == "0") {
                            month = dateString.substring(6, 7)
                        } else {
                            month = dateString.substring(5, 7)
                        }
                        //일
                        if (dateString.substring(8, 9) == "0") {
                            day = dateString.substring(9, 10)
                        } else {
                            day = dateString.substring(8, 10)
                        }
                        var datE: CalendarDay =
                            CalendarDay.from(year.toInt(), month.toInt() - 1, day.toInt())
                        //Log.e("startdate2",datE.toString())
                        calendar.addDecorator(OneDayDecorator(datE))
                    }}



                override fun onFailure(call: Call<scheduleTeamGetBody>, t: Throwable) {
                    Log.e("schedule", "OnFailuer+${t.message}")
                }
            })


    }

    //날짜에 따른 세부 스케쥴 가져오기
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTeamDetailSchedule(rselect: String) {

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeConverter())
            .registerTypeAdapter(LocalDateTime::class.java, object :
                JsonDeserializer<LocalDateTime> {
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


        apiService.get_teamSchedule(App.prefs.teamSeq!!.toLong()).enqueue(object :
            Callback<scheduleTeamGetBody> {
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

                dialogViewN.recycler_scheduleN.adapter = scheduleAdapter
                scheduleAdapter.setItemClickListener(object : scheduleAdapter.ItemClickListener {
                    override fun onClick(view: View, position: Int) {

                       // ava_date.text=
                        //schdeuldeDateString = "" 년월일
                        //   editMessage.setText(time)
                         //   scheduleSeq=scheduleList[position].scheduleSeq

                    } })
            }

            override fun onFailure(call: Call<scheduleTeamGetBody>, t: Throwable) {
                Log.e("schedule", "OnFailuer+${t.message}")
            }
        })
    }


    //알람시간설정
    fun clickNotificationTime(time: String){

        notificationTime = time
        editMessage.setText(time)
        CHECK_NOTIFICATION=false
        CHECK_NOTIFICATION_TIME=true
        Log.e("bot CHECK_NOTIFICATION",CHECK_NOTIFICATION.toString())

    }
}