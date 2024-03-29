package com.example.avatwin.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.avatwin.DataClass.*
import com.example.avatwin.Fragment.Board.BoardDetailFragment
import com.example.avatwin.Fragment.Team.TeamRegisterFragment
import com.example.avatwin.R
import kotlinx.android.synthetic.main.item_board_list.view.*
import kotlinx.android.synthetic.main.item_board_list.view.board_title
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_myteam.view.*
import kotlinx.android.synthetic.main.item_myteam.view.team_description


class commentAdapter():RecyclerView.Adapter<commentAdapter.ViewHolder>(){
    var items= ArrayList<commentBody>()
        interface ItemClickListener {
            fun onClick(view: View, position: Int)
        }

    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):commentAdapter.ViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_comment,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: commentAdapter.ViewHolder, position: Int) {
        val item=items[position]
        holder.setItem(item)

        val BoardDetailFragment = BoardDetailFragment.getInstance()

        holder.itemView.comment_mores.setOnClickListener {
            BoardDetailFragment!!.clickMenu(item)

        }
    }

    override fun getItemCount()=items.size
    fun addItem(item: commentBody){
        items.add(item)
    }
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun setItem(item:commentBody){
            itemView.comment_username.text = item.username
            if(item.content==""){
                itemView.comment_content.visibility= View.GONE
            }
            itemView.comment_content.text = item.content
           // var a= URLDecoder.decode(item.image!!.substring(ApiService.API_URL.length+1), "utf-8");
            Glide.with(itemView).load(item.userImage).into(itemView.comment_userimage)
            Glide.with(itemView).load(item.emoticon).into(itemView.comment_emoticon)

        }
    }

}