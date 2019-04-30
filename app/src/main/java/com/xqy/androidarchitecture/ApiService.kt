package com.xqy.androidarchitecture

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("citys")
    fun getThings(@Body city: String):Call<String>
}