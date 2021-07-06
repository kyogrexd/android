package com.example.project1

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.Utf8.size
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.net.URI.create
import java.net.URL
import java.nio.file.Files.size
import java.security.SignatureException
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.GZIPInputStream
import kotlin.collections.ArrayList
import kotlin.reflect.typeOf


data class Station(
        val StationName: stationName,
        val StationAddress:String,
        val StationPosition : stationpostion
){

    data class  stationpostion(
            val PositionLon: Double,
            val PositionLat: Double
    )
    data class stationName(
            val Zh_tw : String,
            val En : String
    )
}




class MainActivity2 : AppCompatActivity() ,SearchAdapter.OnItemClickListener{

    val myItemList = arrayListOf<Station>()
    private lateinit var adapter :SearchAdapter
    var scope = CoroutineScope(Dispatchers.Default)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        scope.launch {
            secTask()

        }
        sv_station.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })

    }


    fun getServerTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
        return dateFormat.format(calendar.time)
    }
    private suspend fun secTask(){
        try {
            withContext(Dispatchers.Main){
                System.out.println("start")
            }
            var connection: HttpURLConnection? = null
            val APIUrl =
                    "https://ptx.transportdata.tw/MOTC/v2/Rail/THSR/Station?\$top=30&\$format=JSON"
            val APPID = "d0572fa520bd4ff88efc34da7977fab3"
            val APPKEY = "AySDhuhQ3y5h8f15ZXJ4lgxf1Ac"
            val xdate = getServerTime()
            val SignDate = "x-date: $xdate"
            System.out.println(xdate)
            var Signature = ""
            try {
                //取得加密簽章
                Signature = HMAC_SHA1.Signature(SignDate, APPKEY)
            } catch (e1: SignatureException) {
                // TODO Auto-generated catch block
                e1.printStackTrace()
            }

            val sAuth = "hmac username=\"$APPID\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\"$Signature\""
            System.out.println(sAuth)

            try {
                val url = URL(APIUrl)
                connection = url.openConnection() as HttpURLConnection
                connection!!.setRequestMethod("GET")

                connection!!.setRequestProperty("Authorization", sAuth)
                connection!!.setRequestProperty("x-date", xdate)
                connection!!.setRequestProperty("Accept-Encoding", "gzip")
                connection!!.setDoInput(true)

                System.out.println(connection!!.getResponseCode())
                System.out.println(connection!!.getResponseMessage())

                val `in`: BufferedReader
                if ("gzip" == connection.contentEncoding) {
                    val reader = InputStreamReader(GZIPInputStream(connection.inputStream))
                    `in` = BufferedReader(reader)
                } else {
                    val reader = InputStreamReader(connection.inputStream)
                    `in` = BufferedReader(reader)
                }


                // 返回的數據已經過解壓
                //val buffer = StringBuffer()
                var line: String = `in`.readLine()
                System.out.println(line)
                val gson = GsonBuilder().create()
                val obj : List<Station> = gson.fromJson(line,Array<Station>::class.java).toList()

                for(i in 0 until obj.size){
                    myItemList.add(Station(obj[i].StationName,obj[i].StationAddress,obj[i].StationPosition))
                    //System.out.println(obj[i].StationName)
                }
                System.out.println(myItemList)


                `in`.close()



            } catch (e: ProtocolException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            withContext(Dispatchers.Main){
                System.out.println("mid")
                //System.out.println(postionResult)
                
                val linearLayoutManger = LinearLayoutManager(this@MainActivity2)
                linearLayoutManger.orientation = LinearLayoutManager.VERTICAL
                linearLayoutManger.height
                recyclerView.layoutManager = linearLayoutManger

                adapter = SearchAdapter(myItemList,this@MainActivity2)
                recyclerView.adapter = adapter

            }



            withContext(Dispatchers.Main){

                System.out.println("finish")
            }


        }catch (e: Exception) {
            Log.e(localClassName, "cancelled", e)
        }

    }

    override fun onItemClick(position: Int ,Lat: Double ,Lon: Double) {
        //Toast.makeText(this, "Item $position clicked", Toast.LENGTH_SHORT).show()
//        System.out.println(Lat)
//        System.out.println(Lon)
        val b = Bundle()
        b.putDouble("Lat",Lat)
        b.putDouble("Lon",Lon)
        setResult(Activity.RESULT_OK, Intent().putExtras(b))
        finish()
    }


}