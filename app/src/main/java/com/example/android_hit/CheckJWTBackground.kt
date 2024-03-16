package com.example.android_hit

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.android_hit.api.RetrofitClient
import com.example.android_hit.data.TokenResponse
import com.example.android_hit.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class CheckJWTBackground:Service() {
    private lateinit var sharedPref : TokenManager
    override fun onBind(intent: Intent?): IBinder? {
        // Return null because you don't need to bind to the service
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
                    // Handle interruption
                    return@thread
                }
            }
        }

        // Perform your background tasks here
        // This method is called when the service is started
        return START_STICKY  // This makes the service restart if it's killed by the system
    }

    override fun onDestroy() {
        // Clean up resources if needed
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // Stop the service when the app is removed from the recent apps list
        stopSelf()
    }
}