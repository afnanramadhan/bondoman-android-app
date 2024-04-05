package com.example.android_hit.api

import com.example.android_hit.data.LoginPayload
import com.example.android_hit.data.LoginResponse
import com.example.android_hit.data.ScanResponse
import com.example.android_hit.data.TokenResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("/api/auth/login")
    fun login(@Body postData: LoginPayload): Call<LoginResponse>



    @POST("/api/auth/token")
    fun cekToken(@Header("Authorization") token: String): Call<TokenResponse>
    @Multipart
    @POST("/api/bill/upload")
    fun uploadNota(@Header("Authorization") token: String,
                   @Part file: MultipartBody.Part): Call<ScanResponse>

}