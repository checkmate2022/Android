package com.example.avatwin.Fragment.Team

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
import com.example.avatwin.Service.TeamService
import com.example.avatwin.Service.UserService
import kotlinx.android.synthetic.main.dialog_member_search.view.*
import kotlinx.android.synthetic.main.fragment_team_register.*
import kotlinx.android.synthetic.main.fragment_team_register.register_id
import kotlinx.android.synthetic.main.fragment_team_register.register_nickname
import kotlinx.android.synthetic.main.fragment_team_register.register_nickname_check_btn
import kotlinx.android.synthetic.main.fragment_team_register.register_pwd
import kotlinx.android.synthetic.main.fragment_team_register.register_team_button
import kotlinx.android.synthetic.main.fragment_team_register.register_team_list
import kotlinx.android.synthetic.main.fragment_team_update.*
import kotlinx.android.synthetic.main.fragment_team_update.view.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class TeamUpdateFragment(): Fragment() {
    val layoutManager = LinearLayoutManager(activity)
    lateinit var listAdapter: teamListAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_team_update, container, false)


        //팀조회

        root.register_team_list.layoutManager = layoutManager
        listAdapter = teamListAdapter()
        val items: ArrayList<String> = arrayListOf()
        //이름, 설명 조회
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(TeamService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(TeamService::class.java)


        apiService.get_teamId(App.prefs.teamSeq!!.toLong()).enqueue(object : Callback<teamIdGetBody> {
            override fun onResponse(call: Call<teamIdGetBody>, response: Response<teamIdGetBody>) {
                val result = response.body()
                root.register_id.setText(result!!.data.teamName)
                root.register_pwd.setText(result!!.data.teamDescription)

            }

            override fun onFailure(call: Call<teamIdGetBody>, t: Throwable) {
                Log.e("team", "OnFailuer+${t.message}")
            }
        })

        //참여자 조회
        apiService.get_teamUser(App.prefs.teamSeq!!.toLong()).enqueue(object : Callback<teamUserGetBody> {
            override fun onResponse(call: Call<teamUserGetBody>, response: Response<teamUserGetBody>) {
                val result = response.body()

                val len: Int = result!!.list.size
                for(i in 0..len-1){
                    if(result!!.list[i].userId.toString()!=App.prefs.userId){
                    listAdapter.addItem(result!!.list[i].userId.toString())
                    root.register_nickname.setText(register_nickname.getText().toString()+" "+result!!.list[i].userId.toString())}
                }

                root.register_team_list.adapter = listAdapter
            }

            override fun onFailure(call: Call<teamUserGetBody>, t: Throwable) {
                Log.e("team", "OnFailuer+${t.message}")
            }
        })





//팀수정
        //팀삭제
        root.delete_team_button.setOnClickListener {
        deleteTeam()}
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val items: ArrayList<String> = arrayListOf()

 //팀수정
        update_team_button.setOnClickListener {
            val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
            var retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(TeamService.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create()).build()

            var apiService = retrofit.create(TeamService::class.java)


            var data = teamReqBody(register_id.text.toString(),register_pwd.text.toString(),items)
            apiService.put_team(App.prefs.teamSeq!!.toLong(),data).enqueue(object : Callback<teamPostGetBody> {
                override fun onResponse(call: Call<teamPostGetBody>, response: Response<teamPostGetBody>) {
                    val result = response.body()
                    Log.e("성공",result.toString())

                }

                override fun onFailure(call: Call<teamPostGetBody>, t: Throwable) {
                    Log.e("team_put", "OnFailuer+${t.message}")
                }
            })

        }

//검색
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
                                    register_nickname.setText(register_nickname.getText().toString()+" " +mList.list[position].userId.toString())
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

    fun deleteTeam(){

            val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
            var retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(TeamService.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create()).build()

            var apiService = retrofit.create(TeamService::class.java)

            apiService.delete_team(App.prefs.teamSeq!!.toLong()).enqueue(object : Callback<myteamGetBody> {
                override fun onResponse(call: Call<myteamGetBody>, response: Response<myteamGetBody>) {
                    val result = response.body()
                    Log.e("성공",result.toString())
                    val fragmentA = HomeFragment()
                    val bundle = Bundle()
                    fragmentA.arguments=bundle
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.add(R.id.container,fragmentA)
                    transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                    transaction.commit()
                }

                override fun onFailure(call: Call<myteamGetBody>, t: Throwable) {
                    Log.e("team_put", "OnFailuer+${t.message}")
                }
            })


    }
}