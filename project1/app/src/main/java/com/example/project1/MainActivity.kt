package com.example.project1

import android.app.Activity
import android.content.Intent
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

import com.google.gson.GsonBuilder

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.BufferedReader
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



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

                for(i in 0 until obj.size){
                    stationPtList.add(StationPt(obj[i].StationID,obj[i].StationPosition,obj[i].StationName))
                }



                var marker = MarkerOptions()

                for(i in 0 until obj.size) {
                    System.out.println(obj[i].StationPosition.PositionLat)
                    System.out.println(obj[i].StationPosition.PositionLon)
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



}




