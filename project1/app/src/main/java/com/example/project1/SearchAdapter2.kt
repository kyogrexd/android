package com.example.project1


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SearchAdapter2 (private val station2:ArrayList<stationData>, private val listener :OnItemClickListener):
        RecyclerView.Adapter<SearchAdapter2.ViewHolder>() {

    inner class ViewHolder(v:View):RecyclerView.ViewHolder(v), View.OnClickListener{


        val tv_trainNo = v.findViewById<TextView>(R.id.tv_trainNo)
        val tv_originStopTime = v.findViewById<TextView>(R.id.tv_originStopTime)
        val tv_Time = v.findViewById<TextView>(R.id.tv_Time)
        val tv_destinationStopTime = v.findViewById<TextView>(R.id.tv_destinationStopTime)


        init {
            v.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position,station2[position].DailyTrainInfo.TrainNo)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position : Int, TrainNo : String)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.listlayout2,viewGroup,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dateFormat = SimpleDateFormat("HH:mm")
        var startTime : Date = dateFormat.parse(station2[position].OriginStopTime.DepartureTime)
        var endTime : Date = dateFormat.parse(station2[position].DestinationStopTime.ArrivalTime)
        val diff = (endTime.time - startTime.time) / ( 60 * 1000 )
        holder.tv_trainNo.text = station2[position].DailyTrainInfo.TrainNo
        holder.tv_originStopTime.text = station2[position].OriginStopTime.DepartureTime
        holder.tv_Time.text = diff.toString() + "分鐘"
        holder.tv_destinationStopTime.text = station2[position].DestinationStopTime.ArrivalTime
    }

    override fun getItemCount() = station2.size


}