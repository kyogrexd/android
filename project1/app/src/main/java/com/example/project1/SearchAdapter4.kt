package com.example.project1


import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class SearchAdapter4(private val itemName: ArrayList<getResData.Result.Content>, private val lat_St : Double,
                     private val lng_St : Double, private val listener:OnItemClickListener):
        RecyclerView.Adapter<SearchAdapter4.ViewHolder>() {

    inner class ViewHolder(v:View):RecyclerView.ViewHolder(v), View.OnClickListener{


        val tv_Restaurant = v.findViewById<TextView>(R.id.tv_Restaurant)
        val tv_ResAddress = v.findViewById<TextView>(R.id.tv_ResAddress)
        val tv_appraise = v.findViewById<TextView>(R.id.tv_appraise)
        val tv_distance = v.findViewById<TextView>(R.id.tv_distance)
        val img_Res = v.findViewById<ImageView>(R.id.img_Res)

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
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.listlayout4,viewGroup,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_Restaurant.text = "餐廳 : ${itemName[position].name}"
        holder.tv_ResAddress.text = "地址 : ${itemName[position].vicinity}"
        holder.tv_appraise.text = "評價 : ${itemName[position].rating}(${itemName[position].reviewsNumber})"
        val image_url = itemName[position].photo
        Picasso.get().load(image_url).into(holder.img_Res)

        var dis_result = FloatArray(1)
        Location.distanceBetween(lat_St,lng_St,itemName[position].lat,itemName[position].lng,dis_result)
        holder.tv_distance.text = "距離 : ${String.format("%.1f",dis_result[0]/1000)}公里"

    }

    override fun getItemCount() = itemName.size



}