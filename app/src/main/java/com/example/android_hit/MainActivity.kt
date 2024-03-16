package com.example.android_hit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.android_hit.api.RetrofitClient
import com.example.android_hit.data.LoginResponse
import com.example.android_hit.data.TokenResponse
import com.example.android_hit.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPref : TokenManager
    private lateinit var logoutButton : Button
    private lateinit var cekToken : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logoutButton = findViewById(R.id.logoutbutton)
        cekToken = findViewById(R.id.cekToken)

        sharedPref = TokenManager(this)

        logoutButton.setOnClickListener {
            sharedPref.deleteToken()
            Toast.makeText(this, "LOGOUT Success", Toast.LENGTH_SHORT).show()
            goToStart()
        }

        val serviceIntent = Intent(this, CheckJWTBackground::class.java)
        this.startService(serviceIntent)


    }

    private fun goToStart(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}