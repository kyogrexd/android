package com.example.project1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main3.*
import kotlinx.android.synthetic.main.activity_main4.*
import kotlinx.android.synthetic.main.listlayout3.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.net.URL
import java.security.SignatureException
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.GZIPInputStream
import kotlin.collections.ArrayList

data class today_TrainNo(
    val StopTimes : ArrayList<stopTimes>,
){
    data class stopTimes(
        val StopSequence : Int,
        val StationName : stationName,
        val DepartureTime : String
    ){
        data class stationName(
            val Zh_tw : String
        )
    }
}

class MainActivity4 : AppCompatActivity(),SearchAdapter3.OnItemClickListener {
    var scope = CoroutineScope(Dispatchers.Default)
    val myItemList = arrayListOf<today_TrainNo>()
    private lateinit var adapter3 :SearchAdapter3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        scope.launch {
            fourTask()
        }

    }

    fun getServerTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
        return dateFormat.format(calendar.time)
    }

    private suspend fun fourTask() {
        try {
            withContext(Dispatchers.Main) {
                System.out.println("start")
            }
            var trainNO : String = ""
            var startPtName : String = ""
            var endPtName : String = ""
            intent?.extras?.let {
                trainNO = it.getString("TrainNo").toString()
                startPtName = it.getString("startPtName").toString()
                endPtName = it.getString("endPtName").toString()
            }

            var connection: HttpURLConnection? = null
            val APIUrl =
                "https://ptx.transportdata.tw/MOTC/v2/Rail/THSR/DailyTimetable/Today/TrainNo/$trainNO?\$top=30&\$format=JSON"
            val APPID = "d0572fa520bd4ff88efc34da7977fab3"
            val APPKEY = "AySDhuhQ3y5h8f15ZXJ4lgxf1Ac"
            val xdate = getServerTime()
            val SignDate = "x-date: $xdate"
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
                //System.out.println(line)
                val gson = GsonBuilder().create()
                val obj : List<today_TrainNo> = gson.fromJson(line,Array<today_TrainNo>::class.java).toList()

                for(i in 0 until obj.size){
                    myItemList.add(today_TrainNo(obj[i].StopTimes))
                    System.out.println(obj[i].StopTimes)
                }


                `in`.close()



            } catch (e: ProtocolException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            withContext(Dispatchers.Main){
                System.out.println("mid")
                //System.out.println(postionResult)

                val linearLayoutManger = LinearLayoutManager(this@MainActivity4)
                linearLayoutManger.orientation = LinearLayoutManager.VERTICAL
                linearLayoutManger.height
                recyclerView3.layoutManager = linearLayoutManger

                adapter3 = SearchAdapter3(myItemList,startPtName,endPtName,this@MainActivity4)
                recyclerView3.adapter = adapter3

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