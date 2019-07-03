package com.xqy.androidarchitecture

import com.androidx.annotation.ApiService
import com.xqy.androidx.framework.ApiResponse
import retrofit2.http.POST

@ApiService
interface ApiService {
    @POST
    fun bb(): ApiResponse<MutableList<String>>
}