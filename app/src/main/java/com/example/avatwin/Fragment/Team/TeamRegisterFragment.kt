package com.example.avatwin.Fragment.Team

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.avatwin.Adapter.Team.teamListAdapter
import com.example.avatwin.Adapter.Team.teamSearchListAdapter
import com.example.avatwin.Auth.App
import com.example.avatwin.R
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.DataClass.*
import com.example.avatwin.Fragment.HomeFragment
import com.example.avatwin.Service.TeamService
import com.example.avatwin.Service.UserService
import kotlinx.android.synthetic.main.dialog_member_search.view.*
import kotlinx.android.synthetic.main.dialog_schedule_list.*
import kotlinx.android.synthetic.main.fragment_team_register.*
import kotlinx.android.synthetic.main.fragment_team_register.register_id
import kotlinx.android.synthetic.main.fragment_team_register.register_nickname
import kotlinx.android.synthetic.main.fragment_team_register.register_nickname_check_btn
import kotlinx.android.synthetic.main.fragment_team_register.register_pwd
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class TeamRegisterFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_team_register, container, false)



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)
        register_team_list.layoutManager = layoutManager
        lateinit var listAdapter: teamListAdapter
        listAdapter = teamListAdapter()

        val items: ArrayList<String> = arrayListOf()

 //팀등록
        register_team_button.setOnClickListener {
            val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
            var retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(TeamService.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create()).build()

            var apiService = retrofit.create(TeamService::class.java)


            var data = teamReqBody(register_id.text.toString(),register_pwd.text.toString(),items)
            apiService.post_team(data).enqueue(object : Callback<teamPostGetBody> {
                override fun onResponse(call: Call<teamPostGetBody>, response: Response<teamPostGetBody>) {
                    val result = response.body()
                    Log.e("D",result.toString())
                    val fragmentA = HomeFragment()
                    val bundle = Bundle()
                    fragmentA.arguments=bundle
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.add(R.id.container,fragmentA)
                    transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                    transaction.commit()

                }

                override fun onFailure(call: Call<teamPostGetBody>, t: Throwable) {
                    Log.e("avatar_create", "OnFailuer+${t.message}")
                }
            })

        }




        register_nickname_check_btn.setOnClickListener {


            var dlg = AlertDialog.Builder(requireContext())
            var dialogView = View.inflate(context, R.layout.dialog_member_search, null)
            dlg.setView(dialogView)
            dlg.setPositiveButton("확인") { dialog, which ->

            }
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
                    override fun onResponse(call: Call<userGetBody2>, response: Response<userGetBody2>) {
                        if (response.isSuccessful) {
                            var mList = response.body()!!
                            //adapter.addItem(mList.data[0])
                            //Log.e("teamDialog", mList.toString())
                            var userList=ArrayList<userGetBody2>()
                            for (i: joinGetBody in mList.list) {
                                if (i.userId.toString() != App.prefs.userId) {
                                    adapter.addItem(i)
                                }
                            }

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