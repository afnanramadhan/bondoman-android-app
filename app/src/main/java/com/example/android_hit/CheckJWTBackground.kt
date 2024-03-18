package com.example.android_hit

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.android_hit.utils.TokenManager
import retrofit2.Response
import kotlin.concurrent.thread

class CheckJWTBackground:Service() {
    private lateinit var sharedPref : TokenManager
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sharedPref = TokenManager(this)
        thread {
            while (true) {
                try {
                    // Sleep for 2 seconds
                    Thread.sleep(2000)

                    // Perform your task here
                    val exp = sharedPref.getEXP("EXP_TIME")
                    val currentTime = System.currentTimeMillis()/1000
                    Log.d("THREAD", exp.toString())
                    Log.d("THREAD", currentTime.toString())

                    Log.e("THREAD", (exp-currentTime).toString())
                    if(currentTime>exp){
                        sharedPref.deleteToken()
                    }

                } catch (e: InterruptedException) {
                    return@thread
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
}