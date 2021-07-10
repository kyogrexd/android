package com.example.project2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class req_Data{
    var guestKey = ""
    var videoID = ""
    var mode = 0
}

data class getYTApi(
        val result: Result
){
    data class Result(
            val videoInfo: VideoInfo,
    ){
        data class VideoInfo(
                val videourl: String,
                val captionResult: CaptionResult
        )
        data class CaptionResult(
                val results: ArrayList<Results>
        ){
            data class Results(
                    val captions: ArrayList<Captions>
            ){
                data class Captions(
                        val time: Int,
                        val content: String
                )
            }
        }
    }
}

class MainActivity : YouTubeBaseActivity(){
    var scope = CoroutineScope(Dispatchers.Default)
    var getYtUrl = ""
    private lateinit var video_statue : YouTubePlayer
    private lateinit var adapter : listViewAdapter
    val myItemList = arrayListOf<getYTApi.Result.CaptionResult.Results.Captions>()
    var flag = true
    var videoID = ""
    var currentTime : Long = 0
    val linearLayoutManger = LinearLayoutManager(this@MainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var reqData = req_Data()
        reqData.guestKey = "44f6cfed-b251-4952-b6ab-34de1a599ae4"
        reqData.videoID = "5edfb3b04486bc1b20c2851a"
        reqData.mode = 1

        //start_Timer(false)

        val json = Gson().toJson(reqData)
        System.out.println(json)

        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        val req = Request.Builder()
                .url("https://api.italkutalk.com/api/video/detail")
                .post(body).build()

        OkHttpClient().newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("查詢失敗", "$e")
            }

            override fun onResponse(call: Call, response: Response) {
                val reponseData = response.body()?.string()
                //System.out.println(reponseData)
                val obj = Gson().fromJson(reponseData, getYTApi::class.java)
                //System.out.println(obj.result.videoInfo.videourl)
                getYtUrl = obj.result.videoInfo.videourl

                for (i in 0 until obj.result.videoInfo.captionResult.results[0].captions.size) {
                    myItemList.add(
                            getYTApi.Result.CaptionResult.Results.Captions(
                                    obj.result.videoInfo.captionResult.results[0].captions[i].time,
                                    obj.result.videoInfo.captionResult.results[0].captions[i].content
                            )
                    )
                }
                scope.launch {
                    firstTask()
                }
            }
        })



        img_bt.setOnClickListener{
            if (flag){
                video_statue.play()
                img_bt.setImageResource(R.drawable.pause)
                flag = false
            }
            else {
                video_statue.pause()
                img_bt.setImageResource(R.drawable.play)
                flag = true
            }
        }


        listView.setOnItemClickListener { parent, view, position, id ->
            video_statue.loadVideo(videoID, myItemList[position].time * 1000)
            for (i in 0 until listView.childCount){
                listView.getChildAt(i).setBackgroundColor(Color.WHITE)
            }
            if (!flag_Time) {
                video_statue.pause()
                listView.smoothScrollToPositionFromTop(position,0)
                System.out.println("childCount:${listView.childCount}")
                System.out.println("firstVisiblePosition:${listView.firstVisiblePosition}")
                listView.deferNotifyDataSetChanged()
                listView.getChildAt(position - listView.firstVisiblePosition).setBackgroundColor(Color.GREEN)
            }
            flag_Time = false
            s = myItemList[position].time
        }

    }

    private fun intilizePlayer(videoId: String) {
        videoID = videoId
        pv_Yt.initialize(getString(R.string.api_key), object :
                YouTubePlayer.OnInitializedListener
        //YouTubePlayer.PlaybackEventListener
        //YouTubePlayer.PlayerStateChangeListener
        {
            override fun onInitializationSuccess(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubePlayer,
                    p2: Boolean
            ) {
                video_statue = p1
                flag = p2
                if (!p2) {

                    p1.setPlaybackEventListener(object : PlaybackEventListener {
                        override fun onPlaying() {
                            flag_Time = true
                            System.out.println("videoPlay")
                            //currentTime = TimeUnit.MILLISECONDS.toSeconds(video_statue.currentTimeMillis.toLong())
                            img_bt.setImageResource(R.drawable.pause)
                            flag = false
                            start_Timer()
                        }

                        override fun onPaused() {
                            flag_Time = false
                            System.out.println("videoPause")
                            img_bt.setImageResource(R.drawable.play)
                            flag = true

                        }

                        override fun onStopped() {
                        }

                        override fun onBuffering(b: Boolean) {
                        }

                        override fun onSeekTo(i: Int) {
                        }
                    })

                    p1.setPlayerStateChangeListener(object : YouTubePlayer.PlayerStateChangeListener {
                        override fun onLoading() {
                            System.out.println("onLoading")
                        }

                        override fun onLoaded(p0: String?) {

                        }

                        override fun onAdStarted() {
                        }

                        override fun onVideoStarted() {
                            System.out.println("videoStart")
                        }

                        override fun onVideoEnded() {
                            flag_Time = false
                            s = 0
                            System.out.println("videoEnd")
                            img_bt.setImageResource(R.drawable.play)
                            listView.smoothScrollToPositionFromTop(0,0)
                        }

                        override fun onError(p0: YouTubePlayer.ErrorReason?) {
                        }
                    })


                    adapter = listViewAdapter(myItemList)
                    listView.adapter = adapter

//                    linearLayoutManger.orientation = LinearLayoutManager.VERTICAL
//                    linearLayoutManger.height
//                    rec_View.layoutManager = linearLayoutManger
//
//                    adapter = firstAdapter(myItemList, currentTime, this@MainActivity)
//                    rec_View.adapter = adapter
//
                    video_statue!!.loadVideo(videoId)
                    video_statue.play()

                }

            }

            override fun onInitializationFailure(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubeInitializationResult?
            ) {
                Toast.makeText(applicationContext, "error occured", Toast.LENGTH_SHORT).show()
            }


        })
    }

    fun getYoutubeVideoIdFromUrl(inUrl: String) : String? {
        if (inUrl.toLowerCase().contains("youtu.be")){
            return inUrl.substring(inUrl.lastIndexOf("/") + 1)
        }
        val pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
        val compilePattern = Pattern.compile(pattern)
        val matcher = compilePattern.matcher(inUrl)
        return if (matcher.find()) {
            matcher.group()
        }else null
    }

    private suspend fun firstTask(){
        try {
            withContext(Dispatchers.Main) {
                System.out.println("start")
            }

            withContext(Dispatchers.Main){
                System.out.println("mid")
                intilizePlayer(getYoutubeVideoIdFromUrl(getYtUrl)!!)

            }

            withContext(Dispatchers.Main){
                System.out.println("finish")
            }

        }catch (e: Exception) {
            Log.e(localClassName, "cancelled", e)
        }
    }



    override fun onPause() {
        super.onPause()
        scope.coroutineContext.cancel()
    }


//    override fun onItemClick(position: Int, captions_Time: Int) {
//        video_statue.loadVideo(videoID, captions_Time * 1000)
//        if (!flag_Time) {
//            video_statue.pause()
//            linearLayoutManger.scrollToPositionWithOffset(position,0)
//            //rec_View.setSe
//            System.out.println("childCount : ${rec_View.childCount}")
//            for ( i in 0 until rec_View.childCount){
//                rec_View.getChildAt(i).setBackgroundColor(Color.WHITE)
//
//            }
//            if (position != linearLayoutManger.findFirstVisibleItemPosition()){
//                if (rec_View.childCount == 5)  {
//                    if (position - linearLayoutManger.findFirstVisibleItemPosition() > 0){
//                        rec_View.getChildAt(position - linearLayoutManger.findFirstVisibleItemPosition()).setBackgroundColor(Color.BLUE)
//                    }
//                    else
//                    {
//                        rec_View.getChildAt(linearLayoutManger.findFirstVisibleItemPosition() - position).setBackgroundColor(Color.BLUE)
//                    }
//                }
//                else if (rec_View.childCount == 6) {
//                    rec_View.getChildAt(position - linearLayoutManger.findFirstVisibleItemPosition()).setBackgroundColor(Color.BLUE)
//                }
//
//            }
//            else {
//                System.out.println("ok")
//                if (position == 6) {
//                    rec_View.getChildAt(1).setBackgroundColor(Color.BLUE)
//                }
//                else {
//                    if ( rec_View.childCount == 6 ){
//                        rec_View.getChildAt(1).setBackgroundColor(Color.BLUE)
//                    }
//                    else {
//                        rec_View.getChildAt(0).setBackgroundColor(Color.BLUE)
//                    }
//                }
//
//            }
//
//        }
//
//        System.out.println("${position}=====${linearLayoutManger.findFirstVisibleItemPosition()}")

//        flag_Time = false
//        s = captions_Time
//
//    }

    private var m = 0
    private var s = 0
    private var flag_Time = false
    private fun start_Timer(){
        object : Thread(){
            override fun run() {
                while (flag_Time) {
                    try {
                        Thread.sleep(1000)
                    }catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    s += 1
                    if (s >= 60) {
                        s = 0
                        m++
                        if (m >= 60){
                            m = 0
                        }
                    }
                    //System.out.println("${m}:${s}")
                    currentTime = s.toLong() - 1
                    val msg = Message()
                    msg.what = 1
                    mHandler.sendMessage(msg)
                }
            }
        }.start()
    }

    private val mHandler = Handler(Handler.Callback {
        msg ->
        System.out.println("${currentTime} 秒")
        for ( i in 0 until listView.childCount){
            listView.getChildAt(i).setBackgroundColor(Color.WHITE)
        }


        if (0 <= currentTime && currentTime < myItemList[1].time){
            //linearLayoutManger.scrollToPositionWithOffset(0,0)
            listView.smoothScrollToPositionFromTop(0,0)
            chageColor(0)
        }
        else if (myItemList[1].time <= currentTime && currentTime < myItemList[2].time) {
            listView.smoothScrollToPositionFromTop(1,0)
            chageColor(1)
        }
        else if (myItemList[2].time <= currentTime && currentTime < myItemList[3].time) {
            listView.smoothScrollToPositionFromTop(2,0)
            chageColor(2)
        }
        else if (myItemList[3].time <= currentTime && currentTime < myItemList[4].time) {
            listView.smoothScrollToPositionFromTop(3,0)
            chageColor(3)
        }
        else if (myItemList[4].time <= currentTime && currentTime < myItemList[5].time) {
            listView.smoothScrollToPositionFromTop(4,0)
            chageColor(4)
        }
        else if (myItemList[5].time <= currentTime && currentTime < myItemList[6].time) {
            listView.smoothScrollToPositionFromTop(5,0)
            chageColor(5)
        }
        else if (myItemList[6].time <= currentTime && currentTime < myItemList[7].time) {
            listView.smoothScrollToPositionFromTop(6,0)
            System.out.println("第7句")
            chageColor(6)
        }
        else if (myItemList[7].time <= currentTime && currentTime < myItemList[8].time) {
            listView.smoothScrollToPositionFromTop(7,0)
            chageColor(7)
        }
        else if (myItemList[8].time <= currentTime && currentTime < myItemList[9].time) {
            listView.smoothScrollToPositionFromTop(8,0)
            chageColor(8)
        }
        else if (myItemList[9].time <= currentTime && currentTime < myItemList[10].time) {
            listView.smoothScrollToPositionFromTop(9,0)
            chageColor(9)
        }
        else if (myItemList[10].time <= currentTime ) {
            listView.smoothScrollToPositionFromTop(10,0)
            chageColor(10)
            //System.out.println("1010101010101010")
        }

        true
    })

    private fun chageColor(position: Int ){
        listView.deferNotifyDataSetChanged()
        var pos = position - listView.firstVisiblePosition
        //System.out.println(pos)
        if (pos >= 0 && pos < listView.childCount){
            listView.getChildAt(pos).setBackgroundColor(Color.YELLOW)
        }
        else {
            System.out.println("錯誤")
        }

    }
}

