package com.example.lab8

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

data class Contact(
    val name: String?,
    val phone: String?
)

class MainActivity : AppCompatActivity() {
    private  lateinit var  adapter: MyAdapter
    private val contacts = ArrayList<Contact>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.extras?.let{
            if(requestCode ==1 && resultCode == Activity.RESULT_OK){
                contacts.add(Contact(it.getString("name"),it.getString("phone")))
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        recyclerView.layoutManager = linearLayoutManager

        adapter = MyAdapter(contacts)

        recyclerView.adapter = adapter

        btn_add.setOnClickListener {
            startActivityForResult(Intent(this,MainActivity2::class.java),1)
        }

    }



}



