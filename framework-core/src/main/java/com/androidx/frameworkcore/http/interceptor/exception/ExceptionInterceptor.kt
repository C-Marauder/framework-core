package com.androidx.frameworkcore.http.interceptor.exception

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody

/**
 * 异常处理拦截器
 */
internal class ExceptionInterceptor(
    private val mHttpExceptionLiveData: MutableLiveData<HttpException>
) : Interceptor {
    private val jsonParser: JsonParser by lazy {
        JsonParser()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        val path = request.url().encodedPath()
        val body = response.body()
        val code = response.code()
        if (code == 200 && response.isSuccessful && body != null) {
            val content = body.string()
            if (jsonParser.parse(content) !is JsonObject) {
                mHttpExceptionLiveData.value = HttpException("$code", response.message(), path)
            } else {
                val mediaType = body.contentType()
                response = response.newBuilder()
                    .body(ResponseBody.create(mediaType, content))
                    .build()
            }
        } else {
            mHttpExceptionLiveData.value = HttpException("$code", response.message(), path)
        }
        return response
    }
}