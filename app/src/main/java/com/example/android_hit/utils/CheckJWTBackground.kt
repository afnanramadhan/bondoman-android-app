package com.example.android_hit.utils

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.example.android_hit.MainActivity
import kotlin.concurrent.thread

class CheckJWTBackground:Service() {
    private lateinit var sharedPref : TokenManager
    private lateinit var handler: Handler
    private var stop = false
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sharedPref = TokenManager(this)
        handler = Handler()
        thread {
            while (true) {
                try {
                    Thread.sleep(2000)
                    // Perform your task here
                    val exp = sharedPref.getEXP("EXP_TIME")
                    val currentTime = System.currentTimeMillis()/1000
                    Log.d("THREAD", exp.toString())
                    Log.d("THREAD", currentTime.toString())
                    Log.e("THREAD", (exp-currentTime).toString())
                    if(currentTime>exp && !stop){
                        handler.post {
                            showConfirmationBox()
                        }
                        sharedPref.deleteToken()
                        stopSelf()
                        return@thread
                    }
                    if(stop){
                        return@thread
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
        stop = true
        stopSelf()
    }

    private fun showConfirmationBox() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("show_confirmation", true)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
}