package com.example.android_hit.api

import com.example.android_hit.data.LoginPayload
import com.example.android_hit.data.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @POST("/api/auth/login")
    fun login(@Body postData: LoginPayload): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/auth/token")
    fun cekToken(@Body postData: LoginPayload): Call<LoginResponse>
}