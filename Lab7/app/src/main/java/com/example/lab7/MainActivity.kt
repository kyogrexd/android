package com.example.lab7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.adapter_horizintal.view.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val item = ArrayList<Item>()
        var array = resources.obtainTypedArray(R.array.resourceList)

        for(i in 0 until array.length())
            item.add(Item(array.getResourceId(i,0),"水果${i+1}"))
        array.recycle()
        spinner.adapter = MyAdapter(R.layout.adapter_horizintal,item)

        gridview.numColumns = 3
        gridview.adapter = MyAdapter(R.layout.adapter_vertical,item)

        listView.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
            arrayListOf("項目1","項目2","項目3","項目4","項目5","項目6","項目7","項目8","項目9"))

    }
}
data class Item(
    val photo: Int,
    val name: String
)
class MyAdapter constructor(private val layout : Int,private val data : ArrayList<Item>):BaseAdapter(){
    override fun getCount() = data.size


    override fun getItem(position: Int) = data[position]

    override fun getItemId(position: Int) = 0L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(parent?.context,layout,null)
        view.img_photo.setImageResource(data[position].photo)
        view.tv_name.text = data[position].name
        return view
    }

}