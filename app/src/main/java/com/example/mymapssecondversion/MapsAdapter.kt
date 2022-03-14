package com.example.mymapssecondversion

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "MapsAdapter"
class MapsAdapter(val context: Context, val userMaps: List<UserMap>, val onClickListner: OnClickListner) : RecyclerView.Adapter<MapsAdapter.ViewHolder>() {
    interface OnClickListner {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapsAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user_map, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MapsAdapter.ViewHolder, position: Int) {
        val userMap = userMaps[position]
        holder.bind(userMap)
        holder.itemView.setOnClickListener {
            Log.i(TAG, "onBindViewHolder setOnClickListner")
            onClickListner.onItemClick(position)
        }
    }

    override fun getItemCount() = userMaps.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle = itemView.findViewById<TextView>(R.id.itvMapTitle)

        fun bind(userMap: UserMap) {
            textViewTitle.text = userMap.title
        }
    }
}
