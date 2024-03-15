package com.example.android_hit.data.remote

import com.example.android_hit.data.LoginPayload
import com.example.android_hit.data.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/api/auth/login")
    fun login(@Body postData: LoginPayload): Call<LoginResponse>
}