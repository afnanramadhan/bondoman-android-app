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
        val myToken = sharedPref.getToken("TOKEN")!!
        Log.i("TOKEN", myToken)
        cekToken.setOnClickListener {
            RetrofitClient.apiService.cekToken(token = "Bearer $myToken").enqueue(object : Callback<TokenResponse> {
                override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    if (response.isSuccessful) {
                        // Handle successful response
                        val responseData = response.body()
                        Log.i("TOKEN1", responseData?.iat.toString())
                        Log.i("TOKEN1", responseData?.nim.toString())
                        Log.i("TOKEN1", responseData?.exp.toString())
                        Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle unsuccessful response
                        // You can parse the error body using response.errorBody().string()
                        response.errorBody()?.let { it1 -> Log.i("TOKEN1", it1.string()) }
                        Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    // Handle failure
                    // This method is called when the HTTP call fails
                    Toast.makeText(this@MainActivity, "Failed 2", Toast.LENGTH_SHORT).show()
                }
            })

        }

    }

    private fun goToStart(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}