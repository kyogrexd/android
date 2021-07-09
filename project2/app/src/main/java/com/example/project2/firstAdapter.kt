package com.example.project2


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

class firstAdapter(private val itemList : ArrayList<getYTApi.Result.CaptionResult.Results.Captions>,
                   private val currentTime : Long,
                   private val listener : OnItemClickListener) :
    RecyclerView.Adapter<firstAdapter.ViewHolder>() {

    //var selectedItemPosition = 0
    inner class ViewHolder(v: View):RecyclerView.ViewHolder(v), View.OnClickListener{

        var tv_sentence =  v.findViewById<TextView>(R.id.tv_sentence)
        var tv_No =  v.findViewById<TextView>(R.id.tv_No)

        init {
            v.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position, itemList[position].time)

            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position : Int, captions_Time : Int)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.listlayout,viewGroup,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_sentence.text = itemList[position].content
        holder.tv_No.text = "${position+1}"
        holder.itemView.setBackgroundColor(Color.WHITE)




//        holder.itemView.setOnClickListener {
//            selectedItemPosition = position
//            notifyDataSetChanged()
//        }
//        if(selectedItemPosition == position)
//            holder.itemView.setBackgroundColor(Color.parseColor("#DC746C"))
//        else
//            holder.itemView.setBackgroundColor(Color.parseColor("#E49B83"))

    }

    override fun getItemCount() = itemList.size


}