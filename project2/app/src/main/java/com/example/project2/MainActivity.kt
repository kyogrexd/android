package com.example.project2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.regex.Pattern

class MainActivity : YouTubeBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intilizePlayer(getYoutubeVideoIdFromUrl("https://www.youtube.com/watch?v=121gxFxqjPA&t=57s&ab_channel=Chronakai")!!)

    }

    private fun intilizePlayer(videoId : String) {
        pv_Yt.initialize(getString(R.string.api_key),object : YouTubePlayer.OnInitializedListener{
            override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?, p2: Boolean) {
                p1!!.loadVideo(videoId)
                p1.play()
            }

            override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
                Toast.makeText(applicationContext,"error occured",Toast.LENGTH_SHORT).show()
            }

        })
    }
    fun getYoutubeVideoIdFromUrl(inUrl : String) : String? {
        if (inUrl.toLowerCase().contains("youtu.be")){
            return inUrl.substring(inUrl.lastIndexOf("/")+1)
        }
        val pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
        val compilePattern = Pattern.compile(pattern)
        val matcher = compilePattern.matcher(inUrl)
        return if (matcher.find()) {
            matcher.group()
        }else null
    }


}