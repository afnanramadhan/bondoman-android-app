package com.example.android_hit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.android_hit.api.RetrofitClient
import com.example.android_hit.data.LoginPayload
import com.example.android_hit.data.LoginResponse
import com.example.android_hit.data.TokenResponse
import com.example.android_hit.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {

    private lateinit var emailInput : EditText
    private lateinit var passwordInput : EditText
    private lateinit var loginButton : Button

    private lateinit var sharedPref : TokenManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = this.context?.let { TokenManager(it) }!!
        emailInput = view.findViewById(R.id.emailInputField)
        passwordInput = view.findViewById(R.id.passwordInputField)
        loginButton = view.findViewById(R.id.loginButton)

        val isLogin = sharedPref.isLogin("IS_LOGIN")
        if(isLogin){
            goToHome()
        }

        loginButton.setOnClickListener {
            Log.i("TES",emailInput.text.toString())
            Log.i("TES",passwordInput.text.toString())
            val emailValue = emailInput.text.toString()
            val passwordValue = passwordInput.text.toString()
            val data = LoginPayload(emailValue,passwordValue)
            Login(data)

//            val myToken = sharedPref.getToken("TOKEN")!!
//            Log.i("TOKEN", myToken)
//            RetrofitClient.apiService.cekToken(token = "Bearer $myToken").enqueue(object : Callback<TokenResponse> {
//                override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
//                    if (response.isSuccessful) {
//                        // Handle successful response
//                        val responseData = response.body()
//                        Log.i("TOKEN1", responseData?.iat.toString())
//                        Log.i("TOKEN1", responseData?.nim.toString())
//                        Log.i("TOKEN1", responseData?.exp.toString())
//                    } else {
//                        // Handle unsuccessful response
//                        // You can parse the error body using response.errorBody().string()
//                        response.errorBody()?.let { it1 -> Log.i("TOKEN1", it1.string()) }
//                    }
//                }
//
//                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
//                    // Handle failure
//                    // This method is called when the HTTP call fails
//                }
//            })
        }
    }

    private fun goToHome(){
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun Login(data:LoginPayload){
        RetrofitClient.apiService.login(data).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("POST Response", responseBody?.token ?: "")
                    sharedPref.putToken("TOKEN", responseBody!!.token)
                    sharedPref.putIsLogin("IS_LOGIN",true)
                    Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show()
                    sharedPref.getToken("TOKEN")?.let { it1 -> Log.i("LOGIN", it1) }
                    Log.i("LOGIN", sharedPref.isLogin("IS_LOGIN").toString())
                    checkToken(responseBody.token)
                    goToHome()


                } else {
                    Log.e("POST Error", "Failed to make POST request: ${response.message()}")
                    Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("POST Error", "Failed to make POST request: ${t.message}")
            }
        })
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
                    // Handle unsuccessful response
                    // You can parse the error body using response.errorBody().string()
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