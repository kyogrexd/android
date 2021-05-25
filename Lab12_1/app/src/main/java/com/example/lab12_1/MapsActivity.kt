package com.example.lab12_1

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private val REQUEST_PERMISSIONS = 1


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_PERMISSIONS)
        else{
            val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            map.getMapAsync(this)
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            &&ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return

        googleMap.isMyLocationEnabled = true

        var marker = MarkerOptions()

        marker.position(LatLng(25.033611,121.565000))
        marker.title("台北101")
        marker.draggable(true)
        googleMap.addMarker(marker)

        marker.position(LatLng(25.047924,121.517081))
        marker.title("台北車站")
        marker.draggable(true)
        googleMap.addMarker(marker)

        val polylineOpt = PolylineOptions()
        polylineOpt.add(LatLng(25.033611,121.565000))
        polylineOpt.add(LatLng(25.047924,121.517081))
        polylineOpt.add(LatLng(25.032728,121.565137))

        val polyline = googleMap.addPolyline(polylineOpt)
        polyline.width = 10f

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(25.034,121.545),13f))


//        mMap = googleMap
//
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

    }
}