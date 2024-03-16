package com.example.android_hit.api

import com.example.android_hit.data.LoginPayload
import com.example.android_hit.data.LoginResponse
import com.example.android_hit.data.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @POST("/api/auth/login")
    fun login(@Body postData: LoginPayload): Call<LoginResponse>



    @POST("/api/auth/token")
    fun cekToken(@Header("Authorization") token: String): Call<TokenResponse>
}