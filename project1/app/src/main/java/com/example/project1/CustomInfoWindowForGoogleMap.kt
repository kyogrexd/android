package com.example.project1

import android.app.Activity
import android.content.Context
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.activity_main.*

class CustomInfoWindowForGoogleMap(context: Context) : GoogleMap.InfoWindowAdapter {

    var mWindow = (context as Activity).layoutInflater.inflate(R.layout.infowindow,null)


    private fun rendowWindowText(marker: Marker, view: View){

        val StationTitle = view.findViewById<TextView>(R.id.stationST)

        StationTitle.text = marker.title

    }
    override fun getInfoWindow(marker: Marker): View? {
        rendowWindowText(marker,mWindow)
        System.out.println(marker)
        return mWindow
    }

    override fun getInfoContents(marker: Marker): View? {
        rendowWindowText(marker,mWindow)
        return mWindow
    }
}