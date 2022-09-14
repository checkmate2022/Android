package com.example.avatwin.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.avatwin.DataClass.channelBody
import com.example.avatwin.Fragment.ChannelUpdateFragment
import com.example.avatwin.Fragment.Team.TeamRegisterFragment
import com.example.avatwin.R
import kotlinx.android.synthetic.main.item_channel.view.*
import kotlinx.android.synthetic.main.item_team_channel.view.*
import kotlinx.android.synthetic.main.item_team_channel.view.channel_title
import kotlinx.android.synthetic.main.item_team_member_list.view.*


class channelAdapter():RecyclerView.Adapter<channelAdapter.ViewHolder>(){

    var items : ArrayList<channelBody> = arrayListOf()

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
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_channel,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        holder.setItem(item)


        val ChannelUpdateFragment = ChannelUpdateFragment.getInstance()

        holder.itemView.update_button.setOnClickListener {
            ChannelUpdateFragment!!.updateChannel(item,position)

        }

        holder.itemView.delete_button.setOnClickListener {
            items.remove(item)
            ChannelUpdateFragment!!.deleteChannel(item)

        }
    }
    fun clearItem() = items.clear()
    override fun getItemCount()=items.size

    fun addItem(item : channelBody) {items.add(item)}

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun setItem(item: channelBody){
            itemView.channel_title.text = item.channelName

        }
    }

}