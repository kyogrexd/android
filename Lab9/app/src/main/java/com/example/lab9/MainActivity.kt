package com.example.lab9



import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {

    internal var rabprogress = 0
    internal var torprogress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_start.setOnClickListener {
            btn_start.isEnabled = false

            rabprogress = 0
            torprogress = 0

            seekBar.progress = 0
            seekBar2.progress = 0

            runThread()
            //runAsyncTask()
            MyTask(this).execute()

        }
    }
    private fun runThread(){
        object :Thread(){
            override fun run(){
                while(rabprogress <= 100 && torprogress < 100){
                    try {
                        Thread.sleep(100)
                    }catch (e: InterruptedException){
                        e.printStackTrace()
                    }
                    rabprogress += (Math.random()*3).toInt()
                    System.out.println("兔")
                    val msg = Message()
                    msg.what = 1
                    mHandler.sendMessage(msg)
                }

            }
        }.start()
    }



    private val mHandler = Handler(Handler.Callback { msg ->
        when (msg.what) {
            1 -> seekBar.progress = rabprogress
        }

        if (rabprogress >= 100 && torprogress < 100) {
            Toast.makeText(this, "兔子勝利", Toast.LENGTH_SHORT).show()
            btn_start.isEnabled = true
        }
        true
    })


    private class MyTask
    internal constructor(context: MainActivity) : AsyncTask<Void, Int, String>() {

        private val activityReference: WeakReference<MainActivity> = WeakReference(context)
        val activity = activityReference.get()

        override fun doInBackground(vararg params: Void?): String{
            if (activity != null) {
                while (activity.torprogress <= 100 && activity.rabprogress < 100){
                    try {
                        Thread.sleep(100)
                    }catch (e:InterruptedException){
                        e.printStackTrace()
                    }
                    System.out.println("doInBackground")
                    activity.torprogress += (Math.random()*3).toInt()
                    publishProgress(activity.torprogress)
                }
            }
            return "task finished"
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            values[0]?.let {
                if (activity != null) {
                    activity.seekBar2.progress = it
                }
            }
        }

        override fun onPostExecute(result: String?) {

            if (activity != null) {
                if(activity.torprogress >= 100 && activity.rabprogress < 100){
                    Toast.makeText(activity,"烏龜勝利",Toast.LENGTH_SHORT).show()
                    activity.btn_start.isEnabled = true
                }
            }
        }
    }

//    private fun runAsyncTask(){
//        object : AsyncTask<Void, Int, Boolean>(){
//            override fun doInBackground(vararg params: Void?): Boolean {
//                while (torprogress <= 100 && rabprogress < 100){
//                    try {
//                        Thread.sleep(100)
//                    }catch (e:InterruptedException){
//                        e.printStackTrace()
//                    }
//                    System.out.println("doInBackground")
//                    torprogress += (Math.random()*3).toInt()
//                    publishProgress(torprogress)
//                }
//                return true
//            }
//
//            override fun onProgressUpdate(vararg values: Int?) {
//                super.onProgressUpdate(*values)
//                values[0]?.let {
//                    seekBar2.progress = it
//                }
//            }
//
//            override fun onPostExecute(result: Boolean?) {
//                if(torprogress >= 100 && rabprogress < 100){
//                    Toast.makeText(this@MainActivity,"烏龜勝利",Toast.LENGTH_SHORT).show()
//                    btn_start.isEnabled = true
//                }
//            }
//        }
//    }
}