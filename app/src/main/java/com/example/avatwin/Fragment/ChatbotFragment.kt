package com.example.avatwin.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.fragment_chatbot.*
import kotlinx.android.synthetic.main.fragment_chatbot.view.*
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
                addMessageToList(message, false)
                sendMessageToBot(message)
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

    private fun sendMessageToBot(message: String) {
       /*   val s = QueryParameters.newBuilder().setPayload(Struct.newBuilder().putFields("userId",
        Value.newBuilder().setStringValue(App.prefs.userId).build())
        .build()).build()*/
     val queryInput = QueryInput.newBuilder()
            .setText(TextInput.newBuilder().setText(message).setLanguageCode("ko"))
            .build()
    val payloadBuilder = Struct.newBuilder()
   // val googleBuilder = Struct.newBuilder()
    //googleBuilder.putFields("richResponse", Value.newBuilder().setStringValue(App.prefs.userId).build())
    //payloadBuilder.putFields("google", Value.newBuilder().setStructValue(googleBuilder).build())


          val s = QueryParameters.newBuilder().setPayload(Struct.newBuilder().putFields("userId",
              Value.newBuilder().setStringValue(App.prefs.userId).build()).putFields("www",
              Value.newBuilder().setStringValue("ww").build())
              .build()).build()

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
    }
}