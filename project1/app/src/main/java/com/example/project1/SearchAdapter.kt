package com.example.project1


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.sql.Array
import java.util.*
import kotlin.collections.ArrayList


class SearchAdapter (private val station1:ArrayList<Station>,private val listener :OnItemClickListener):
        RecyclerView.Adapter<SearchAdapter.ViewHolder>() ,Filterable{
    var stationList  = ArrayList<Station>()
    init {
        stationList = station1
    }
    inner class ViewHolder(v:View):RecyclerView.ViewHolder(v), View.OnClickListener{


        val name_st = v.findViewById<TextView>(R.id.tv_train)
        val address = v.findViewById<TextView>(R.id.textView4)

        init {
            v.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position,stationList[position].StationPosition.PositionLat,stationList[position].StationPosition.PositionLon)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int,Lat: Double,Lon: Double)
    }

    @ExperimentalStdlibApi
    override fun getFilter() : Filter{
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    stationList = station1
                }
                else {
                    val resultList = ArrayList<Station>()
                    for (row in stationList){
                        if (row.StationName.Zh_tw.lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    stationList = resultList
                }
                val filterReslts = FilterResults()
                filterReslts.values = stationList
                return filterReslts
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                stationList = results?.values as ArrayList<Station>
                notifyDataSetChanged()
            }

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.listlayout,viewGroup,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name_st.text = stationList[position].StationName.Zh_tw + "站"
        holder.address.text = stationList[position].StationAddress
    }

    override fun getItemCount() = stationList.size


}


