package com.xqy.androidarchitecture

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("categories")
    fun getThings():Call<String>
}