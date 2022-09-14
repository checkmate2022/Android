package com.example.testchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.avatwin.Constant
import com.example.avatwin.DataClass.Chat
import com.example.avatwin.R


class ChatAdapter(val context: Context)
    : RecyclerView.Adapter<ChatAdapter.Holder>()
{
    val MY_VIEW = 1
    val RECEIVE_VIEW = 2
    val ENTER_VIEW = 3

    val constant = Constant
    var receiverImage=""
    var chatDatas = ArrayList<Chat>()

    fun addItem(item: Chat,image:String){
        chatDatas.add(item)
        receiverImage=image
        notifyDataSetChanged()
    }

    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView){

        val mid = itemView.findViewById<TextView>(R.id.mid)
        val chatItem = itemView.findViewById<TextView>(R.id.messageTextView)
        val senderImage = itemView.findViewById<ImageView>(R.id.sender_img)
        fun bind(chatData: Chat, context: Context){
            val viewType = itemViewType

            when(viewType){
                ENTER_VIEW -> chatItem.text = chatData.message
                MY_VIEW -> {
                    //mid.text = chatData.sender
                    chatItem.text = chatData.message}
                else -> {
                    //senderImage
                    Glide.with(itemView).load(receiverImage).into(senderImage)
                    mid.text = chatData.sender
                    chatItem.text = chatData.message
                }
            }
        }
    }

    override fun getItemViewType(position: Int) : Int{
        val currentItem: Chat = chatDatas[position]
        val msgType = currentItem.type
        val sender = currentItem.sender

        when(msgType){
            constant.MESSAGE_TYPE_ENTER -> {
                return ENTER_VIEW
            }
            constant.MESSAGE_TYPE_TALK -> {
                when(sender){
                    constant.SENDER -> return MY_VIEW
                    else -> return RECEIVE_VIEW
                }
            }
        }

        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val view = when(viewType){
            MY_VIEW -> LayoutInflater.from(context).inflate(R.layout.item_my_msg, parent, false)
            RECEIVE_VIEW -> LayoutInflater.from(context).inflate(R.layout.item_receive_msg, parent, false)
            else -> LayoutInflater.from(context).inflate(R.layout.item_enter_msg, parent, false)
        }

        return Holder(view)
    }

    override fun getItemCount(): Int {
        return chatDatas.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(chatDatas[position], context)
    }

    fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
        val smoothScroller = object: LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int {
                return snapMode
            }

            override fun getHorizontalSnapPreference(): Int {
                return snapMode
            }
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }
}