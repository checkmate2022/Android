package com.example.avatwin.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.avatwin.DataClass.*
import com.example.avatwin.R
import kotlinx.android.synthetic.main.item_myteam.view.*
import kotlinx.android.synthetic.main.item_myteam.view.team_name
import kotlinx.android.synthetic.main.item_team_channel.view.*
import kotlinx.android.synthetic.main.item_team_member_list.view.*
import kotlinx.android.synthetic.main.item_team_member_list.view.member_title


class teamMenuAdapter():RecyclerView.Adapter<teamMenuAdapter.ViewHolder>(){

    var items : ArrayList<String> = arrayListOf()

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):teamMenuAdapter.ViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_team_channel,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: teamMenuAdapter.ViewHolder, position: Int) {
        val item=items[position]
        holder.setItem(item)

        holder.itemView.setOnClickListener {
            itemClickListner.onClick(it, position)

        }
    }
    fun clearItem() = items.clear()
    override fun getItemCount()=items.size

    fun addItem(item : String) {items.add(item)}

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun setItem(item:String){
            itemView.channel_title.text = item

        }
    }

}