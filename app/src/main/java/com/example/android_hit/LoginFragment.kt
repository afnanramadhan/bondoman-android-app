package com.example.android_hit

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.android_hit.data.LoginPayload
import com.example.android_hit.data.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {

    private lateinit var emailInput : EditText
    private lateinit var passwordInput : EditText
    private lateinit var loginButton : Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailInput = view.findViewById<EditText>(R.id.emailInputField)
        passwordInput = view.findViewById<EditText>(R.id.passwordInputField)
        loginButton = view.findViewById<Button>(R.id.loginButton)


        loginButton.setOnClickListener {
            Log.i("TES",emailInput.text.toString())
            Log.i("TES",passwordInput.text.toString())
            val emailValue = emailInput.text.toString()
            val passwordValue = passwordInput.text.toString()
            hitLoginApi(emailValue,passwordValue)
        }
    }

    private fun hitLoginApi(email:String, password:String) {
        val data = LoginPayload(email,password) // Your data object to be sent in the request

        RetrofitClient.apiService.login(data).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("POST Response", responseBody?.token ?: "")
                } else {
                    Log.e("POST Error", "Failed to make POST request: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("POST Error", "Failed to make POST request: ${t.message}")
            }
        })
    }





}