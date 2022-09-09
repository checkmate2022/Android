package com.example.avatwin.Adapter.Chatbot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.avatwin.DataClass.teamaBody
import com.example.avatwin.R
import kotlinx.android.synthetic.main.item_chatbot_list.view.*


class chatbotItemListAdapter(var item:ArrayList<teamaBody>, val context: Context):RecyclerView.Adapter<chatbotItemListAdapter.ViewHolder>(){

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
        val itemView=LayoutInflater.from(context).inflate(R.layout.item_chatbot_list,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        holder.setItem(item)
        holder.itemView.setOnClickListener {
            itemClickListner.onClick(it, position)

        }

    }
    fun clearItem() = items.clear()
    override fun getItemCount()=items.size

    fun addItem(item : teamaBody) {items.add(item)}

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun setItem(item: teamaBody){
            itemView.chatbot_item_txt.text = item.teamName

        }
    }

}