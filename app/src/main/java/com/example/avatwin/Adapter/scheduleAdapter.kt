package com.example.avatwin.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.avatwin.DataClass.*
import com.example.avatwin.R
import kotlinx.android.synthetic.main.item_avatar.view.*
import kotlinx.android.synthetic.main.item_avatar.view.ia_name
import kotlinx.android.synthetic.main.item_myteam.view.*
import kotlinx.android.synthetic.main.item_team_schedule.view.*


class scheduleAdapter():RecyclerView.Adapter<scheduleAdapter.ViewHolder>(){
    var items=ArrayList<scheduleBody>()
        interface ItemClickListener {
            fun onClick(view: View, position: Int)
        }

    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):scheduleAdapter.ViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_team_schedule,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: scheduleAdapter.ViewHolder, position: Int) {
        val item=items[position]
        holder.setItem(item)

        holder.itemView.setOnClickListener {
            itemClickListner.onClick(it, position)

        }
    }

    override fun getItemCount()=items.size

    fun addItem(item: scheduleBody){
        items.add(item)
    }

    fun clearItem(){
        items.clear()
    }
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun setItem(item: scheduleBody){
            itemView.schedule_start.text = item.scheduleStartDate.toString().substring(11,16)
            itemView.schedule_end.text = item.scheduleEndDate.toString().substring(11,16)
            itemView.schedule_content.text = item.scheduleName
            //schedule_start
           // var a= URLDecoder.decode(item.image!!.substring(ApiService.API_URL.length+1), "utf-8");
            //Glide.with(itemView).load(item.image!!).into(itemView.limg)

        }
    }

}