package com.example.avatwin.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.avatwin.Fragment.Schedule.ScheduleRegisterFragment
import com.example.avatwin.R
import kotlinx.android.synthetic.main.item_team_member_list.view.*


class scheduleMemberAdapter():RecyclerView.Adapter<scheduleMemberAdapter.ViewHolder>(){

    val items : ArrayList<String> = arrayListOf()

        interface ItemClickListener {
            fun onClick(view: View, position: Int)
        }

    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_team_member_list,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        holder.setItem(item)
        val ScheduleRegisterFragment = ScheduleRegisterFragment.getInstance()

        holder.itemView.member_delete_button.setOnClickListener {
            items.remove(item)
            ScheduleRegisterFragment!!.deleteMember(position)
        //연결 fragment에 함수 작성하고 adapter notify -> edittext글 내용바꾸기

        }
    }
    fun clearItem() = items.clear()
    override fun getItemCount()=items.size

    fun addItem(item : String) {items.add(item)}

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun setItem(item:String){
            itemView.member_title.text = item

        }
    }

}