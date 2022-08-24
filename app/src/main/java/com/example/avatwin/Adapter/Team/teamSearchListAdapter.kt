package com.example.avatwin.Adapter.Team

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.avatwin.DataClass.*
import com.example.avatwin.R
import kotlinx.android.synthetic.main.item_team_member_list.view.*


class teamSearchListAdapter :RecyclerView.Adapter<teamSearchListAdapter.ViewHolder>(){

    val items = ArrayList<joinGetBody>()

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
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_team_search_list,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        holder.setItem(item)

        holder.itemView.setOnClickListener {
            itemClickListner.onClick(it, position)

        }

        holder.itemView.member_delete.setOnClickListener{
            Log.e("deleteTest","Test")
        }
    }
    fun clearItem() = items.clear()
    override fun getItemCount()=items.size

    fun addItem(item : joinGetBody) {items.add(item)}

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){


        fun setItem(item:joinGetBody){
            itemView.member_title.text = item.userId

        }
    }

}