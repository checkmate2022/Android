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
import com.example.avatwin.Auth.App
import com.example.avatwin.DataClass.*
import com.example.avatwin.R
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.dialogflow.v2.*
import com.google.protobuf.Struct
import com.google.protobuf.Value
import kotlinx.android.synthetic.main.dialog_member_search.view.*
import kotlinx.android.synthetic.main.dialog_schedule_date.view.*
import kotlinx.android.synthetic.main.fragment_chatbot.*
import kotlinx.android.synthetic.main.fragment_chatbot.view.*
import kotlinx.android.synthetic.main.fragment_schedule_register.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    var startDateString = ""
    var startTimeString = ""
    var endDateString = ""
    var endTimeString = ""
    var start = ""
    var end = ""
    var scheduleType=""
    var scheduleTitle=""

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
                    Log.e("bot","o")
                    //sendFianlMessageToBot("총일정 "+scheduleType)
                    sendFianlMessageToBot("총일정 "+start+" "+end+" "+scheduleType)
                    CHECK_DATE=false
                }else {
                    Log.e("Check_Date",CHECK_DATE.toString())
                    Log.e("bot","그냥보내기")
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
            ).putFields(
                "ww",
                Value.newBuilder().setStringValue("www").build()
            ).build()
        ).build()

    /*
    val payloadBuilder = Struct.newBuilder()
    val googleBuilder = Struct.newBuilder()
    val messageList = ListValue.newBuilder()
    products.forEach { product ->
        val message = Intent.Message.newBuilder()
        val basicCard = Intent.Message.BasicCard.newBuilder()
        basicCard.formattedText = product.getShortDescr()
        basicCard.title = product.getName()
        val image = Intent.Message.Image.newBuilder()
        image.imageUri = product.getMediaURLMedium()
        basicCard.setImage(image)
        message.setBasicCard(basicCard)
        messageList.addValuesBuilder().setField(
            Intent.getDescriptor().findFieldByNumber(Intent.MESSAGES_FIELD_NUMBER),
            message.build()
        )
    }
    googleBuilder.putFields("richResponse", Value.newBuilder().setListValue(messageList).build())
    payloadBuilder.putFields("google", Value.newBuilder().setStructValue(googleBuilder).build())
    responseBuilder.setPayload(payloadBuilder)*/
        GlobalScope.launch {
            sendMessageInBg(queryInput,s)
        }
    }
    private fun sendFianlMessageToBot(message: String) {

        val queryInput = QueryInput.newBuilder()
            .setText(TextInput.newBuilder().setText(message).setLanguageCode("ko"))
            .build()
        Log.e("bot","총일정 "+start+" "+end+" "+scheduleType)
        //팀seq,일정명,  시작날짜 type 끝날짜
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
            start=" 시작 "+startDateString+" "+startTimeString
            end=" 끝 "+endDateString+" "+endTimeString
            editMessage.setText(start+" "+end)
            CHECK_DATE=true
            Log.e("Check_Date",CHECK_DATE.toString())
        }
        dlg.show()
    }
}