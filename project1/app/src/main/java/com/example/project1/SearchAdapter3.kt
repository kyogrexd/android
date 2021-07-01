package com.example.project1


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SearchAdapter3 (private val station3:ArrayList<today_TrainNo>, private val listener :OnItemClickListener):
        RecyclerView.Adapter<SearchAdapter3.ViewHolder>() {

    inner class ViewHolder(v:View):RecyclerView.ViewHolder(v), View.OnClickListener{

        val tv_TrainNoST = v.findViewById<TextView>(R.id.tv_TrainNoST)
        val tv_TrainNoSTime = v.findViewById<TextView>(R.id.tv_TrainNoSTime)

        init {
            v.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position : Int)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.listlayout3,viewGroup,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_TrainNoST.text = "${station3[0].StopTimes[position].StopSequence}. ${station3[0].StopTimes[position].StationName.Zh_tw}站"
        holder.tv_TrainNoSTime.text = station3[0].StopTimes[position].DepartureTime
    }

    override fun getItemCount() = station3[0].StopTimes.size


}