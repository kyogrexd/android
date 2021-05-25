package com.example.lab15

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyMessageinService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("Firebase","onNewToken $token")
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)
        Log.e("Firebase","onMessageReceived")


        msg?.let {
            //Log.e("Firebase",)
            for(entry in it.data.entries){
                Log.e("message","${entry.key}/${entry.value}")
                System.out.println(entry)
            }
        }
    }
}

