package com.example.android_hit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.Timer
import java.util.TimerTask

class BondoManSplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bondo_man_splash_screen)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                startActivity(Intent(this@BondoManSplashScreen, LoginActivity::class.java))
                finish()
            }
        }, 3000)

    }
}