package com.androidx.frameworkcore.http.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * 头部拦截器
 */
class HeaderInterceptor(private val addHeader:(builder:Request.Builder)->Unit):Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .apply {
                addHeader(this)
            }.build()
        return chain.proceed(request)

    }
}