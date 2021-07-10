package com.example.project2

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.listlayout.view.*

class listViewAdapter (private val itemList : ArrayList<getYTApi.Result.CaptionResult.Results.Captions>) : BaseAdapter(){
    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0L
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(parent?.context,R.layout.listlayout,null)
        view.tv_sentence.text = itemList[position].content
        view.tv_No.text = "${position+1}"
        view.setBackgroundColor(Color.WHITE)
        return view
    }



}