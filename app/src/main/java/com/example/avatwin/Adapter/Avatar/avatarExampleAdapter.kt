package com.example.avatwin.Adapter.Avatar

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.avatwin.R

class avatarExampleAdapter(var items: ArrayList<Int>) :
    RecyclerView.Adapter<avatarExampleAdapter.PagerViewHolder>() {


    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.item_avatar_example, parent, false)) {
        val item = itemView.findViewById<ImageView>(R.id.item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder((parent))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.item.setImageResource(items[position])
    }
}