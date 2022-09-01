package com.example.avatwin.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.INotificationSideChannel
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.avatwin.Adapter.Team.teamAdapter
import com.example.avatwin.Adapter.chatbotAdapter
import com.example.avatwin.Auth.App
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.DataClass.*
import com.example.avatwin.Fragment.Team.TeamMainFragment
import com.example.avatwin.Fragment.Team.TeamRegisterFragment
import com.example.avatwin.R
import com.example.avatwin.Service.ApiService
import com.example.avatwin.Service.FcmService
import com.example.avatwin.Service.TeamService
import com.google.api.Advice.newBuilder
import com.google.api.Advice.parseFrom
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.dialogflow.v2.*
import com.google.protobuf.Struct
import com.google.protobuf.Value
import kotlinx.android.synthetic.main.fragment_chatbot.*
import kotlinx.android.synthetic.main.fragment_chatbot.view.*
import kotlinx.android.synthetic.main.fragment_fcm_list.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_team_register.*
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

import java.util.*
import kotlin.collections.ArrayList

class ChatbotFragment:Fragment() {
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

    @SuppressLint("NotifyDataSetChanged")
    private fun addMessageToList(message: String, isReceived: Boolean) {
        messageList.add(Message(message, isReceived))
        editMessage.setText("")
        chatAdapter.notifyDataSetChanged()
        chatView.layoutManager?.scrollToPosition(messageList.size - 1)
    }

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
/*
 .setEvent(EventInput.newBuilder()
                .setLanguageCode("en-US")
                .setParameters(
                    Struct.newBuilder().putFields("userId",
                        Value.newBuilder().setStringValue(App.prefs.userId).build())
                        .build())
                .build())

                  .setEvent(EventInput.newBuilder()
                    .setName("REVISION")
                    .setParameters(
                        Struct.newBuilder()
                            .putFields("username",
                                Value.newBuilder()
                                    .setStringValue("jihye")
                                    .build())
                            .build())
                    .setLanguageCode("eu-US"))
 */
    private fun sendMessageToBot(message: String) {
        val queryInput = QueryInput.newBuilder()
            .setText(TextInput.newBuilder().setText(message).setLanguageCode("ko"))
            .build()
    val s = QueryParameters.newBuilder().setPayload(Struct.newBuilder().putFields("userId",
        Value.newBuilder().setStringValue(App.prefs.userId).build())
        .build()).build()
      //  val input = QueryInput.newBuilder()
        //    .setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build()

        GlobalScope.launch {
            sendMessageInBg(queryInput,s)
        }
    }

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

    private fun updateUI(response: DetectIntentResponse) {
        val botReply: String = response.queryResult.fulfillmentText
        if (botReply.isNotEmpty()) {
            addMessageToList(botReply, true)
        } else {
            //Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show()
        }
    }
}