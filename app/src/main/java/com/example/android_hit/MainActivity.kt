package com.example.android_hit

//import androidx.appcompat.app.AlertDialog
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.android_hit.api.RetrofitClient
import com.example.android_hit.data.LoginPayload
import com.example.android_hit.data.LoginResponse
import com.example.android_hit.data.TokenResponse
import com.example.android_hit.databinding.ActivityMainBinding
import com.example.android_hit.utils.CryptoManager
import com.example.android_hit.utils.TokenManager
import com.example.android_hit.utils.UserManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userPref : UserManager
    private lateinit var sharedPref : TokenManager
    private lateinit var cryptoManager: CryptoManager

    private lateinit var dialog : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        userPref = UserManager(this)
        sharedPref = TokenManager(this)
        cryptoManager = CryptoManager()

        val networkManager = NetworkManager(this)
        var networkDialog: AlertDialog? = null

        networkManager.observe(this) { isConnected ->
            if (!isConnected) {
                networkDialog = showNetworkDialog()
            } else {
                networkDialog?.dismiss()
                networkDialog = null
            }
        }
        setContentView(binding.root)

        setCurrentFragment(Transaction(), HeaderTransaction())
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_transaction -> {
//                    if(!networkManager.value!!){
//                        setCurrentFragment(NetworkError(), HeaderNetwork())
//                    }else{
//                        setCurrentFragment(Transaction(), HeaderTransaction())
//                    }
                    setCurrentFragment(Transaction(), HeaderTransaction())
                }
                R.id.nav_scan -> {
                    setCurrentFragment(Scan(),  HeaderScan())
                }
                R.id.nav_graphs -> {
                    setCurrentFragment(Graphs(), HeaderGraphs())
                }
                R.id.nav_settings -> {
                    setCurrentFragment(Settings(), HeaderSettings())
                }
            }
            true
        }

        //Keluarin pop up kalo token dah abis, sama matiin background service
        if (intent.getBooleanExtra("show_confirmation", false)) {
            val serviceIntent = Intent(this, CheckJWTBackground::class.java)
            this.stopService(serviceIntent)
            showConfirmationDialog()
        }

    }
    private fun showNetworkDialog(): AlertDialog {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("No Internet Connection")
        alertDialogBuilder.setMessage("Please check your internet connection")
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        return alertDialog
    }

    private fun setCurrentFragment(fragment: Fragment, header: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment)
            replace(R.id.header_layout, header)
            commit()
        }
    private fun showConfirmationDialog() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Token Expired")
        alertDialogBuilder.setMessage("Your token has expired. Do you want to renew it?")
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            reLogin()
            finish()
        }

        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            goToStart()
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun goToStart(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun reLogin(){
        val emailValue = userPref.getEmail("EMAIL")!!
        val passwordValue = userPref.getPassword("PASS")!!
        val passDecrpt = cryptoManager.decrypt(passwordValue)
        Log.e("RELOG", "email $emailValue")
        Log.e("RELOG", "pass $passwordValue")
        Log.e("RELOG","passd $passDecrpt")

        val data = LoginPayload(emailValue,passDecrpt)
        RetrofitClient.apiService.login(data).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    sharedPref.putToken("TOKEN", responseBody!!.token)
                    sharedPref.putIsLogin("IS_LOGIN",true)
                    Toast.makeText(this@MainActivity, "Login Success", Toast.LENGTH_SHORT).show()

                    checkToken(responseBody.token)
                    val serviceIntent = Intent(this@MainActivity, CheckJWTBackground::class.java)
                    this@MainActivity.startService(serviceIntent)

                } else {
                    Log.e("POST Error", "Failed to make POST request: ${response.message()}")
                    Toast.makeText(this@MainActivity, "Login Failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("POST Error", "Failed to make POST request: ${t.message}")
            }
        })

        Log.e("RELOG","test 123")
    }

    private fun checkToken(token: String){
        RetrofitClient.apiService.cekToken(token = "Bearer $token").enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (response.isSuccessful) {
                    // Handle successful response
                    val responseData = response.body()
                    if (responseData != null) {
                        sharedPref.putNIM("NIM_USER",responseData.nim)
                        Log.i("TOKEN1", responseData.nim)
                    }
                    if (responseData != null) {
                        sharedPref.putEXP("EXP_TIME",responseData.exp)
                        Log.i("TOKEN1", responseData.exp.toString())
                    }


                } else {
                    response.errorBody()?.let { it1 -> Log.i("TOKEN1", it1.string()) }
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                // Handle failure
                // This method is called when the HTTP call fails
            }
        })
    }
}
