package com.example.project1

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main2.*

import kotlinx.android.synthetic.main.activity_main5.*
import kotlinx.coroutines.*

import okhttp3.*
import java.io.*


class Data{
    var lastIndex  = 0
    var count = 0
    var type = intArrayOf(0)
    var lat = 0.0
    var lng = 0.0
    var range = ""
}

//class getResData{
//    lateinit var results: Result
//    class Result{
//        lateinit var content :Array<Content>
//        class Content{
//            val name = ""
//            val rating = ""
//            val vicinity = ""
//        }
//    }
//}
data class getResData(
        val results : Result
){
    data class Result(
            val content : ArrayList<Content>
    ){
        data class Content(
                val name : String,
                val rating : String,
                val vicinity : String,
                val reviewsNumber : Int,
                val photo : String,
                val lat : Double,
                val lng : Double
        )
    }
}






class MainActivity5 : AppCompatActivity(),SearchAdapter4.OnItemClickListener {
    var scope = CoroutineScope(Dispatchers.Default)

    private lateinit var adapter4 :SearchAdapter4

    val myItemList = arrayListOf<getResData.Result.Content>()
    var lat : Double = 0.0
    var lng : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)


        intent?.extras?.let {
            lat = it.getDouble("lat").toDouble()
            lng = it.getDouble("lng").toDouble()
        }

        var data = Data()
        data.lastIndex = -1
        data.count = 15
        data.type = intArrayOf(7)
        data.lat = lat
        data.lng = lng
        data.range = "2000"

        val json = Gson().toJson(data)
        System.out.println(json)

        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json)
        val req = Request.Builder()
                .url("https://api.bluenet-ride.com/v2_0/lineBot/restaurant/get")
                .post(body).build()

        OkHttpClient().newCall(req).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("查詢失敗","$e")
            }

            override fun onResponse(call: Call, response: Response) {
                val resultData = response.body()?.string()
                //System.out.println(resultData)
                System.out.println("response")
                val obj = Gson().fromJson(resultData,getResData::class.java)
                for (i in 0 until obj.results.content.size) {
                    myItemList.add(getResData.Result.Content(obj.results.content[i].name,obj.results.content[i].rating,
                        obj.results.content[i].vicinity,obj.results.content[i].reviewsNumber,obj.results.content[i].photo,
                        obj.results.content[i].lat, obj.results.content[i].lng))
                }
                scope.launch {
                    fiveTask()
                }
            }
        })




    }



    override fun onPause() {
        super.onPause()
        scope.coroutineContext.cancel()
    }


    private suspend fun fiveTask() {
        try {
            withContext(Dispatchers.Main) {
                System.out.println("start")

            }



            withContext(Dispatchers.Main){
                System.out.println("mid")
                System.out.println(myItemList)

                val linearLayoutManger = LinearLayoutManager(this@MainActivity5)
                linearLayoutManger.orientation = LinearLayoutManager.VERTICAL
                linearLayoutManger.height
                recyclerView4.layoutManager = linearLayoutManger

                adapter4 = SearchAdapter4(myItemList, lat, lng,this@MainActivity5)
                recyclerView4.adapter = adapter4


            }

            withContext(Dispatchers.Main){
                System.out.println("finish")


            }

        }catch (e: Exception) {
            Log.e(localClassName, "cancelled", e)
        }
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }
}






