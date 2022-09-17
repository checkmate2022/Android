package com.example.avatwin.Adapter.Avatar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.avatwin.DataClass.avatarBody
import com.example.avatwin.Fragment.MyPageFragment
import com.example.avatwin.Fragment.Team.TeamUpdateFragment
import com.example.avatwin.R
import kotlinx.android.synthetic.main.item_avatar.view.*
import kotlinx.android.synthetic.main.item_team_member_list.view.*


class avatarAdapter(var item:ArrayList<avatarBody>):RecyclerView.Adapter<avatarAdapter.ViewHolder>(){
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
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_avatar,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        holder.setItem(item)

        val MyPageFragment = MyPageFragment.getInstance()

        //기본설정
        holder.itemView.basic_button.setOnClickListener {
            holder.itemView.basic_button.visibility = View.GONE
            MyPageFragment!!.basicAvatar(item.avatarSeq)
        }

        //삭제
        holder.itemView.delete_button.setOnClickListener {
            items.remove(item)
            MyPageFragment!!.deleteAvatar(item.avatarSeq)
        }
        }

    override fun getItemCount()=items.size

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun setItem(item:avatarBody){
            itemView.ia_name.text = item.avatarName

           // var a= URLDecoder.decode(item.image!!.substring(ApiService.API_URL.length+1), "utf-8");
            //Glide.with(itemView).load(item.image!!).into(itemView.limg)

        }
    }

}