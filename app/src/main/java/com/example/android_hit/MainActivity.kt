package com.example.android_hit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.android_hit.utils.TokenManager

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPref : TokenManager
    private lateinit var logoutButton : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logoutButton = findViewById(R.id.logoutbutton)

        sharedPref = TokenManager(this)

        logoutButton.setOnClickListener {
            sharedPref.deleteToken()
            Toast.makeText(this, "LOGOUT Success", Toast.LENGTH_SHORT).show()
            goToStart()
        }

    }

    private fun goToStart(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}