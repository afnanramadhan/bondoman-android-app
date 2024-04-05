package com.example.android_hit.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.android_hit.utils.CheckJWTBackground
import com.example.android_hit.MainActivity
import com.example.android_hit.R
import com.example.android_hit.api.RetrofitClient
import com.example.android_hit.data.LoginPayload
import com.example.android_hit.data.LoginResponse
import com.example.android_hit.data.TokenResponse
import com.example.android_hit.utils.CryptoManager
import com.example.android_hit.utils.TokenManager
import com.example.android_hit.utils.UserManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {

    private lateinit var emailInput : EditText
    private lateinit var passwordInput : EditText
    private lateinit var loginButton : Button

    private lateinit var sharedPref : TokenManager
    private lateinit var userPref : UserManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = this.context?.let { TokenManager(it) }!!
        userPref = this.context?.let {UserManager(it)}!!
        val cryptoManager = CryptoManager()
        emailInput = view.findViewById(R.id.emailInputField)
        passwordInput = view.findViewById(R.id.passwordInputField)
        loginButton = view.findViewById(R.id.loginButton)

        val isLogin = sharedPref.isLogin("IS_LOGIN")
        if(isLogin){
            goToHome()
        }


        loginButton.setOnClickListener {
            val context = context
            if (context != null && isNetworkAvailable(context)) {
                val emailValue = emailInput.text.toString()
                val passwordValue = passwordInput.text.toString()
                val passEncrypt = cryptoManager.encrypt(passwordValue)
                userPref.putEmail("EMAIL", emailValue)
                userPref.putPassword("PASS", passEncrypt)
                val data = LoginPayload(emailValue,passwordValue)
                login(data)
            } else {
                (activity as? LoginActivity)?.showNetworkDialog()
            }
        }
    }

    private fun goToHome(){
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }


    //tembak api login
    private fun login(data:LoginPayload){
        RetrofitClient.apiService.login(data).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    sharedPref.putToken("TOKEN", responseBody!!.token)
                    sharedPref.putIsLogin("IS_LOGIN",true)
                    Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show()

                    checkToken(responseBody.token)
                    val serviceIntent = Intent(context, CheckJWTBackground::class.java)
                    context?.startService(serviceIntent)
                    goToHome()


                } else {
                    val errMes = response.errorBody()?.string()
                    Log.e("POST Error", "Failed to make POST request: $errMes")
                    Toast.makeText(context, "Login Failed: $errMes", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("POST Error", "Failed to make POST request: ${t.message}")
            }
        })
    }

    //tembak api auth/token
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
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }




}