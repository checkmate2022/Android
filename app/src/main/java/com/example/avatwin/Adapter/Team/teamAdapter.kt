package com.example.avatwin.Adapter.Team

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.avatwin.DataClass.teamaBody
import com.example.avatwin.R
import kotlinx.android.synthetic.main.item_myteam.view.*


class teamAdapter(var item:ArrayList<teamaBody>):RecyclerView.Adapter<teamAdapter.ViewHolder>(){
    var items=item
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
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_myteam,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        holder.setItem(item)

        holder.itemView.setOnClickListener {
            itemClickListner.onClick(it, position)

        }
    }

    override fun getItemCount()=items.size

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun setItem(item:teamaBody){
            itemView.team_name.text = item.teamName
            itemView.team_description.text = item.teamDescription

           // var a= URLDecoder.decode(item.image!!.substring(ApiService.API_URL.length+1), "utf-8");
            //Glide.with(itemView).load(item.image!!).into(itemView.limg)

        }
    }

}