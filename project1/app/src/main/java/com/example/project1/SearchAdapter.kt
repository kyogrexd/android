package com.example.project1


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class SearchAdapter (private val station1:ArrayList<Station>,private val listener :OnItemClickListener):
        RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(v:View):RecyclerView.ViewHolder(v), View.OnClickListener{


        val name_st = v.findViewById<TextView>(R.id.tv_train)
        val address = v.findViewById<TextView>(R.id.textView4)

        init {
            v.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position,station1[position].StationPosition.PositionLat,station1[position].StationPosition.PositionLon)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int,Lat: Double,Lon: Double)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.listlayout,viewGroup,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name_st.text = station1[position].StationName.Zh_tw + "站"
        holder.address.text = station1[position].StationAddress
    }

    override fun getItemCount() = station1.size


}