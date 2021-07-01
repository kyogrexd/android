package com.example.project1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_main3.*
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

data class stationData(
    val DailyTrainInfo : dailyTrainInfo,
    val OriginStopTime : originStopTime,
    val DestinationStopTime : destinationStopTime
) {
    data class dailyTrainInfo(
        val TrainNo: String
    )

    data class originStopTime(
        //val ArrivalTime : String,
        val DepartureTime: String,
    )

    data class destinationStopTime(
        val ArrivalTime: String,
        //val DepartureTime : String
    )
}
class MainActivity3 : AppCompatActivity(),SearchAdapter2.OnItemClickListener {

    var scope = CoroutineScope(Dispatchers.Default)
    val myItemList = arrayListOf<stationData>()
    private lateinit var adapter2 :SearchAdapter2
    private lateinit var startPtName : String
    private lateinit var endPtName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        scope.launch {
            thirdTask()

        }

    }

    fun getServerTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
        return dateFormat.format(calendar.time)
    }

    fun getToday(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd", Locale.US)
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
        return dateFormat.format(calendar.time)
    }

    private suspend fun thirdTask(){
        try {
            withContext(Dispatchers.Main){
                System.out.println("start")
            }
            var startPt :String = ""
            var endPt : String = ""

            val today = getToday()
            intent?.extras?.let {
                startPt = it.getString("startPt").toString()
                endPt = it.getString("endPt").toString()
                startPtName = it.getString("startPtName").toString()
                endPtName = it.getString("endPtName").toString()
            }



            var connection: HttpURLConnection? = null
            val APIUrl =
                "https://ptx.transportdata.tw/MOTC/v2/Rail/THSR/DailyTimetable/OD/$startPt/to/$endPt/$today?\$top=30&\$format=JSON"
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
                System.out.println(line)
                val gson = GsonBuilder().create()
                val obj : List<stationData> = gson.fromJson(line,Array<stationData>::class.java).toList()

                for(i in 0 until obj.size){
                    myItemList.add(stationData(obj[i].DailyTrainInfo,obj[i].OriginStopTime,obj[i].DestinationStopTime))
                    //System.out.println(obj[i].DestinationStopTime)
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

                val linearLayoutManger = LinearLayoutManager(this@MainActivity3)
                linearLayoutManger.orientation = LinearLayoutManager.VERTICAL
                linearLayoutManger.height
                recyclerView2.layoutManager = linearLayoutManger

                adapter2 = SearchAdapter2(myItemList,this@MainActivity3)
                recyclerView2.adapter = adapter2

                tv_way.text = "${startPtName} >>>>> ${endPtName}"


            }



            withContext(Dispatchers.Main){

                System.out.println("finish")
            }


        }catch (e: Exception) {
            Log.e(localClassName, "cancelled", e)
        }

    }

    override fun onItemClick(position: Int, TrainNo : String) {
        System.out.println(TrainNo)
    }
}
