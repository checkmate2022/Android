package com.example.avatwin.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.avatwin.DataClass.boardTeamBody
import com.example.avatwin.DataClass.teamBody
import com.example.avatwin.DataClass.teamGetBody
import com.example.avatwin.DataClass.teamaBody
import com.example.avatwin.R
import kotlinx.android.synthetic.main.item_board_list.view.*
import kotlinx.android.synthetic.main.item_myteam.view.*
import kotlinx.android.synthetic.main.item_myteam.view.team_description


class boardAdapter():RecyclerView.Adapter<boardAdapter.ViewHolder>(){
    var items= ArrayList<boardTeamBody>()
        interface ItemClickListener {
            fun onClick(view: View, position: Int)
        }

    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):boardAdapter.ViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_board_list,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: boardAdapter.ViewHolder, position: Int) {
        val item=items[position]
        holder.setItem(item)

        holder.itemView.setOnClickListener {
            itemClickListner.onClick(it, position)

        }
    }

    override fun getItemCount()=items.size


    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun setItem(item:boardTeamBody){
            itemView.board_title.text = item.title
            itemView.board_username.text = item.username
            itemView.board_time.text = item.createDate.toString()
           // var a= URLDecoder.decode(item.image!!.substring(ApiService.API_URL.length+1), "utf-8");
            //Glide.with(itemView).load(item.image!!).into(itemView.limg)

        }
    }

}