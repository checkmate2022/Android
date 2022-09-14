package com.example.avatwin.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.avatwin.DataClass.fcmBody
import com.example.avatwin.Fragment.Schedule.ScheduleRegisterFragment
import com.example.avatwin.R
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.item_fcm_list.view.*
import kotlinx.android.synthetic.main.item_team_member_list.view.*


class fcmAdapter():RecyclerView.Adapter<fcmAdapter.ViewHolder>(){

    val items : ArrayList<fcmBody> = arrayListOf()

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
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_fcm_list,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        holder.setItem(item)

    }
    fun clearItem() = items.clear()
    override fun getItemCount()=items.size

    fun addItem(item : fcmBody) {items.add(item)}

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun setItem(item: fcmBody){
            itemView.notification_title.text = item.title
            itemView.notification_date.text = item.notificationDate.toString().substring(0,10)+" "+item.notificationDate.toString().substring(11,16)
            itemView.notification_body.text = item.body
            itemView.notification_userId.text = item.userId


        }
    }

}