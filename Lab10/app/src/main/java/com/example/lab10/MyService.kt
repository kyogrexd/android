package com.example.lab10

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MyService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        Thread(Runnable {
            try {
                Thread.sleep(5000)

                val intent = Intent(this@MyService,MainActivity2::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                this@MyService.startActivity(intent)

            }catch (e:InterruptedException){
                e.printStackTrace()
            }
        }).start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return Service.START_NOT_STICKY
    }

}