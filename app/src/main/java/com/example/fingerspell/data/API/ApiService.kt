package com.example.fingerspell.data.API


import com.example.fingerspell.data.LoginResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("history")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    )
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String
    ):ResponseBody
    @POST("Login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse
}

