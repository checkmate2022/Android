package com.example.avatwin.Fragment.Board

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.avatwin.Adapter.commentAdapter
import com.example.avatwin.Auth.*
import com.example.avatwin.Converter.LocalDateTimeConverter
import com.example.avatwin.DataClass.*
import com.example.avatwin.R
import com.example.avatwin.Service.AvatarService
import com.example.avatwin.Service.BoardService
import com.example.avatwin.Service.CommentService
import com.example.avatwin.Service.ScheduleService
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.*
import kotlinx.android.synthetic.main.dialog_comment_menu.*
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.fragment_board.iv1
import kotlinx.android.synthetic.main.fragment_board.iv1_image
import kotlinx.android.synthetic.main.fragment_board.iv2
import kotlinx.android.synthetic.main.fragment_board.iv2_image
import kotlinx.android.synthetic.main.fragment_board.iv3
import kotlinx.android.synthetic.main.fragment_board.iv3_image
import kotlinx.android.synthetic.main.fragment_board.iv4
import kotlinx.android.synthetic.main.fragment_board.iv4_image
import kotlinx.android.synthetic.main.fragment_board.view.*
import kotlinx.android.synthetic.main.fragment_board.view.emoticon_add_button
import kotlinx.android.synthetic.main.fragment_board_list.view.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.fragment_mypage.view.*
import kotlinx.android.synthetic.main.item_comment.*
import kotlinx.android.synthetic.main.item_comment.view.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDateTime

class BoardDetailFragment(): Fragment() {

    init{ instance = this }

    companion object{
        private var instance: BoardDetailFragment? = null
        fun getInstance(): BoardDetailFragment?
        { return instance  }}
    var selectEmoticonUrl=""
    var url1=""
    var url2=""
    var url3=""
    var url4=""
    lateinit var adapter: commentAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_board, container, false)
        val layoutManager = LinearLayoutManager(requireActivity())
        root.recyclerview_comment.layoutManager = layoutManager

        //init
        initBoard()
        root.emoticon_group.isVisible=false
        root.emoticon_add_button.setOnClickListener{
            root.emoticon_group.isVisible=true
            //기본 아바타 이모티콘 설정해주기
            setEmoticon()
            //이모티콘 클릭 시 채팅에
            selectEmoticon()
        }

        //수정
        root.board_update.setOnClickListener {
            val fragmentA = BoardUpdateFragment()
            val bundle = Bundle()
            fragmentA.arguments=bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }

        //삭제
        root.board_delete.setOnClickListener{
            deleteBoard()
        }

        //댓글
        root.comment_button.setOnClickListener{
            registerComment(root.comment_text.text.toString(),selectEmoticonUrl)
        }

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initBoard(){

        val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeConverter()).create()


        //채널별 게시글 가져오기
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BoardService.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        var apiService = retrofit.create(BoardService::class.java)
        var tests = apiService.get_BoardById(App.prefs.boardSeq!!.toLong())
        tests.enqueue(object : Callback<boardGetBodyById> {
            override fun onResponse(call: Call<boardGetBodyById>, response: Response<boardGetBodyById>) {
                if (response.isSuccessful) {
                    var mList = response.body()!!
                    Log.e("board",mList.data.toString())

                    //날짜
                    var date = mList.data.createDate.toString().substring(0,10)+" "+mList.data.createDate.toString().substring(11,16)
                    Log.i("board",date)
                    board_detail_date.setText(date)
                    //제목
                    board_detail_title.setText(mList.data.title)
                    //이미지
                    //board_detail_image
                    Log.i("boardDetail",mList.data.userImage.toString())
                    Log.i("boardDetail2",mList.data.userImg.toString())
                    Glide.with(view!!).load(mList.data.userImage).into(board_detail_image)
                    //별명
                    board_detail_name.setText(mList.data.username)
                    //내용
                    board_detail_content.setText(mList.data.content)
                    //댓글 리스트
                    adapter = commentAdapter()
/*
                    for(i:commentBody in mList.data.comments){
                        if(i.username==App.prefs.userId){
                            comment_mores.visibility=View.VISIBLE
                        }else{
                            comment_mores.visibility=View.GONE
                        }
                    }*/

                    adapter.items=mList.data.comments
                    recyclerview_comment.adapter= adapter
                } }

            override fun onFailure(call: Call<boardGetBodyById>, t: Throwable) {
                Log.e("team", "OnFailuer+${t.message}")
            } })

    }

    fun deleteBoard(){
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BoardService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(BoardService::class.java)

        apiService.delete_board(App.prefs.boardSeq!!.toLong()).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val result = response.body()
                Log.e("성공",result.toString())
                val fragmentA = BoardMainFragment()
                val bundle = Bundle()
                fragmentA.arguments=bundle
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.add(R.id.container,fragmentA)
                transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                transaction.commit()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("team_put", "OnFailuer+${t.message}")
            }
        })

    }


    fun registerComment(content:String,selectEmoticonUrl:String){
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(CommentService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(CommentService::class.java)

        apiService.post_comment(App.prefs.boardSeq!!.toLong(),content,selectEmoticonUrl).enqueue(object : Callback<commentGetBody> {
            override fun onResponse(call: Call<commentGetBody>, response: Response<commentGetBody>) {
                val result = response.body()
                //Log.e("성공",result.toString())
                emoticon_group.visibility=View.GONE
                adapter.addItem(result!!.data)
                comment_text.setText("")
            }

            override fun onFailure(call: Call<commentGetBody>, t: Throwable) {
                Log.e("COMMENT", "OnFailuer+${t.message}")
            }
        })

    }
    //댓글 메뉴클릭
    fun clickMenu(item: commentBody){
        lateinit var tnrBottomSheetDialog: BottomSheetDialog
        val layoutManager = LinearLayoutManager(activity)
        val tnrBottomSheetView = layoutInflater.inflate(R.layout.dialog_comment_menu, null)

        tnrBottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogCustomTheme) // dialog에 sytle 추가
        tnrBottomSheetDialog.setContentView(tnrBottomSheetView)
        tnrBottomSheetDialog.show()

        //수정 버튼 눌렀을 때
        tnrBottomSheetDialog.dialog_update.setOnClickListener {
            tnrBottomSheetDialog.dismiss()
            val fragmentA = CommentUpdateFragment(item)
            val bundle = Bundle()
            fragmentA.arguments = bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragmentA)
            transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
            transaction.commit()
        }

        //삭제 버튼 눌렀을 떄
        tnrBottomSheetDialog.dialog_delete.setOnClickListener {

            var dlg = AlertDialog.Builder(requireContext())
            var dialogView = View.inflate(context, R.layout.dialog_delete, null)
            dlg.setView(dialogView)
            dlg.setPositiveButton("확인") { dialog, which ->

                tnrBottomSheetDialog.dismiss()
                val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
                var retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(ScheduleService.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create()).build()

                var apiService = retrofit.create(CommentService::class.java)


                apiService.delete_comment(item.commentSeq!!).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val result = response.body()
                        adapter.items.remove(item)
                        recyclerview_comment.adapter!!.notifyDataSetChanged()
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

    fun setEmoticon(){
        val defaultImage = R.drawable.profile_none
        //기본아바타
        val url = "https://cdn.pixabay.com/photo/2021/08/03/07/03/orange-6518675_960_720.jpg"


        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit2 = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(AvatarService.API_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        var apiService2 = retrofit2.create(AvatarService::class.java)
        var myavatar = apiService2.get_myAvatar()

        myavatar.enqueue(object : Callback<myAvatarRes> {
            override fun onResponse(call: Call<myAvatarRes>, response: Response<myAvatarRes>) {
                if (response.isSuccessful) {
                    var res = response.body()!!
                    Log.e("avatar", res.toString())

                    for(i: avatarBody in res.list){
                        Log.e("avatar", i.isBasic.toString())
                        if(i.isBasic==true) {
                            Log.e("avatar", i.emoticons[0].emoticonUrl!!)
                            url1= i.emoticons[0].emoticonUrl!!
                            url2= i.emoticons[1].emoticonUrl!!
                            url3= i.emoticons[2].emoticonUrl!!
                            url4= i.emoticons[3].emoticonUrl!!
                            Glide.with(requireContext())
                                .load(url1) // 불러올 이미지 url
                                .placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
                                .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
                                .fallback(defaultImage) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                                .circleCrop() // 동그랗게 자르기
                                .into(iv1_image)
                            Glide.with(requireContext())
                                .load(url2) // 불러올 이미지 url
                                .placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
                                .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
                                .fallback(defaultImage) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                                .circleCrop() // 동그랗게 자르기
                                .into(iv2_image)

                            Glide.with(requireContext())
                                .load(url3) // 불러올 이미지 url
                                .placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
                                .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
                                .fallback(defaultImage) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                                .circleCrop() // 동그랗게 자르기
                                .into(iv3_image)

                            Glide.with(requireContext())
                                .load(url4) // 불러올 이미지 url
                                .placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
                                .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
                                .fallback(defaultImage) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                                .circleCrop() // 동그랗게 자르기
                                .into(iv4_image)
                        }
                    }
                } }

            override fun onFailure(call: Call<myAvatarRes>, t: Throwable) {
                Log.e("avatar", "OnFailuer+${t.message}")
            } })


    }

    fun selectEmoticon(){
        //이미지 선택
        iv1.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                iv2.setChecked(false)
                iv3.setChecked(false)
                iv4.setChecked(false)
                selectEmoticonUrl = url1
            }
        })
        iv2.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                iv1.setChecked(false)
                iv3.setChecked(false)
                iv4.setChecked(false)
                selectEmoticonUrl = url2
            }
        })
        iv3.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                iv1.setChecked(false)
                iv2.setChecked(false)
                iv4.setChecked(false)
                selectEmoticonUrl = url3
            }
        })
        iv4.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                iv1.setChecked(false)
                iv2.setChecked(false)
                iv3.setChecked(false)
                selectEmoticonUrl = url4
            }

        })
    }
}

