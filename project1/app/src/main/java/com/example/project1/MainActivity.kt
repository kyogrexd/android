package com.example.project1

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager

import android.os.Bundle

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson

import com.google.gson.GsonBuilder

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import okhttp3.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

import java.net.HttpURLConnection
import java.net.ProtocolException
import java.net.URL
import java.security.SignatureException
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.GZIPInputStream

data class StationPt(
        val StationID : String,
        val StationPosition: stationpostion,
        val StationName: stationName
){
    data class  stationpostion(
            val PositionLon: Double,
            val PositionLat: Double
    )
    data class stationName(
            val Zh_tw: String,
            val En: String
    )
}

//class Data{
//    var lastIndex  = 0
//    var count = 0
//    var type = intArrayOf(0)
//    var lat = 0.0
//    var lng = 0.0
//    var range = ""
//}
//class getResData{
//    lateinit var results: Result
//    class Result{
//        lateinit var content :Array<Content>
//        class Content{
//            val name = ""
//            val rating = 0.0
//            val vicinity = ""
//            val reviewsNumber = 0
//            var lat = 0.0
//            var lng = 0.0
//        }
//    }
//}


class MainActivity : AppCompatActivity() , OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener {


    private val REQUEST_PERMISSIONS = 1
    private lateinit var map: GoogleMap
    var scope = CoroutineScope(Dispatchers.Default)

    val stationPtList = arrayListOf<StationPt>()



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty()) return
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                for (result in grantResults)
                    if (result != PackageManager.PERMISSION_GRANTED)
                        finish()
                    else {
                        val map =
                                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                        map.getMapAsync(this)
                    }
            }
        }
    }


//    private val receiver: BroadcastReceiver = object :
//        BroadcastReceiver(){
//        override fun onReceive(context: Context, intent: Intent) {
//            intent.extras?.getString("json").let {
//                val data = Gson().fromJson(it,getResData::class.java)
//                val items = arrayOfNulls<String>(data.results.content.size)
//
//                for (i in 0 until data.results.content.size){
//                    items[i] = "\n餐廳:${data.results.content[i].name}\n地址:${data.results.content[i].vicinity}" +
//                            "\n評價:${data.results.content[i].rating}(${data.results.content[i].reviewsNumber})\n"
//                    this@MainActivity.runOnUiThread {
//                        AlertDialog.Builder(this@MainActivity)
//                            .setTitle("附近餐廳").setNegativeButton("ok",null)
//                            .setItems(items,null)
//                            .show()
//                    }
//                }
//            }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val intenfilter = IntentFilter("MyMessage")
//        registerReceiver(receiver,intenfilter)

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSIONS)
        else {
            val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            map.getMapAsync(this)
        }

        btn_sw.setOnClickListener {
            var tptext = ""
            if(ed_start.length() > 1 && ed_end.length() > 1){
                tptext = ed_start.text.toString()
                ed_start.setText(ed_end.text)
                ed_end.setText(tptext)
            }
        }

        btn_search.setOnClickListener {
            startActivityForResult(Intent(this, MainActivity2::class.java), 1)
        }

        var match_startID :String = ""
        var match_endID :String = ""
        var match_startName :String = ""
        var match_endName :String = ""
        button3.setOnClickListener {

            if (ed_start.text.toString() != ed_end.text.toString() && ed_start.text.toString() != "" && ed_end.text.toString() != "") {
                //startActivityForResult(Intent(this,MainActivity3::class.java), 1)\
                for (i in 0 until stationPtList.size) {
                    //System.out.println(stationPtList[i].StationName.Zh_tw)
                    if (ed_start.text.toString() == (stationPtList[i].StationName.Zh_tw + "站")) {
                        match_startID = stationPtList[i].StationID
                        match_startName = stationPtList[i].StationName.Zh_tw + "站"
                    }
                    if (ed_end.text.toString() == (stationPtList[i].StationName.Zh_tw + "站")) {
                        match_endID = stationPtList[i].StationID
                        match_endName = stationPtList[i].StationName.Zh_tw + "站"
                    }
                }
                val bundle = Bundle()
                bundle.putString("startPt",match_startID)
                bundle.putString("endPt",match_endID)
                bundle.putString("startPtName",match_startName)
                bundle.putString("endPtName",match_endName)
                val i = Intent(this,MainActivity3::class.java)
                i.putExtras(bundle)
                startActivity(i)
            }

        }



    }

    override fun onPause() {
        super.onPause()
        scope.coroutineContext.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.extras?.let{
            if (requestCode == 1 && resultCode == Activity.RESULT_OK){
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.getDouble("Lat"), it.getDouble("Lon")), 13f))

            }
        }
    }



    override fun onMapReady(googleMap: GoogleMap) {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return

        //val getLat = intent.getBundleExtra("")
        scope.launch() {
            myTask(googleMap)
        }
        map = googleMap

        googleMap.isMyLocationEnabled = true
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(25.034, 121.545), 13f))
        googleMap?.setOnInfoWindowClickListener(this)
        //googleMap?.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(this))



    }




    override fun onInfoWindowClick(marker: Marker) {
        //marker.showInfoWindow()
        System.out.println(marker.position.latitude)
        System.out.println(marker.position.longitude)
        val popupMenu = PopupMenu(this, button3)
        popupMenu.menuInflater.inflate(R.menu.menu_context, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            val id = menuItem.itemId
//            if (id == R.id.station){
//                System.out.println(marker.title)
//            }
            if (id == R.id.startSt){
                System.out.println("起點")
                ed_start.setText(marker.title)
            }
            else if (id == R.id.endSt){
                System.out.println("終點")
                ed_end.setText(marker.title)
            }
            else if (id == R.id.restaurant){
                System.out.println("餐廳")
                System.out.println(marker.position.latitude)
                System.out.println(marker.position.longitude)
                val bundle = Bundle()
                bundle.putDouble("lat",marker.position.latitude)
                bundle.putDouble("lng",marker.position.longitude)
                val i = Intent(this,MainActivity5::class.java)
                i.putExtras(bundle)
                startActivity(i)


//                val data = Data()
//                data.lastIndex = -1
//                data.count = 15
//                data.type = intArrayOf(7)
//                data.lat = marker.position.latitude
//                data.lng = marker.position.longitude
//                data.range = "2000"
//
//                val json = Gson().toJson(data)
//                System.out.println(json)
//
//                val body = RequestBody.create(
//                    MediaType.parse("application/json; charset=utf-8"),json)
//                val req = Request.Builder()
//                    .url("https://api.bluenet-ride.com/v2_0/lineBot/restaurant/get")
//                    .post(body).build()
//                OkHttpClient().newCall(req).enqueue(object : Callback {
//                    override fun onFailure(call: Call, e: IOException) {
//                        Log.e("查詢失敗","$e")
//                    }
//                    override fun onResponse(call: Call, response: Response) {
//                        sendBroadcast(Intent("MyMessage").putExtra("json",response.body()?.string()))
//                    }
//                })
            }
            false
        }
        popupMenu.show()

    }


    fun getServerTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
        return dateFormat.format(calendar.time)
    }




    private suspend fun myTask(googleMap: GoogleMap){
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

            } catch (e: ProtocolException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }



            withContext(Dispatchers.Main){
                val `in`: BufferedReader
                if ("gzip" == connection!!.contentEncoding) {
                    val reader = InputStreamReader(GZIPInputStream(connection.inputStream))
                    `in` = BufferedReader(reader)
                } else {
                    val reader = InputStreamReader(connection.inputStream)
                    `in` = BufferedReader(reader)
                }


                // 返回的數據已經過解壓
                //val buffer = StringBuffer()
                var line: String = `in`.readLine()

                val gson = GsonBuilder().create()
                val obj : List<StationPt> = gson.fromJson(line, Array<StationPt>::class.java).toList()
                System.out.println(obj)
                for(i in 0 until obj.size){
                    stationPtList.add(StationPt(obj[i].StationID,obj[i].StationPosition,obj[i].StationName))
                }

                var marker = MarkerOptions()

                for(i in 0 until obj.size) {
//                    System.out.println(obj[i].StationPosition.PositionLat)
//                    System.out.println(obj[i].StationPosition.PositionLon)
                    marker.position(LatLng(obj[i].StationPosition.PositionLat, obj[i].StationPosition.PositionLon))
                    marker.title(obj[i].StationName.Zh_tw + "站")
                    marker.draggable(true)
                    googleMap.addMarker(marker)
                }
                `in`.close()
                System.out.println("finish")
            }
            

        }catch (e: Exception) {
            Log.e(localClassName, "cancelled", e)
        }

    }
//    override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(receiver)
//    }



}




