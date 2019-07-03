package com.xqy.androidx.framework.interceptor
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody

/**
 * 响应体拦截器
 */
class BodyInterceptor(private val convert: (content: String) -> String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        val body = response.body()
        if (response.isSuccessful && response.code() == 200 && body != null) {
            val mediaType = body.contentType()
            val content = body.string()
            val bodyStr = convert(content)
            response = response.newBuilder()
                .body(ResponseBody.create(mediaType,bodyStr))
                .build()
        }

        return response
    }
}