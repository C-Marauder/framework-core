package com.xqy.androidx.framework.interceptor

import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer

/**
 * 加密拦截器
 */
class EncryptInterceptor(private val isAllow:(path:String)->Boolean,
                         private val encode:(param:String)->String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if ("POST" == request.method()) {
            val requestBody = request.body()
            if (requestBody is FormBody) {

            } else {
                val isInterceptor = isAllow(request.url().encodedPath())
                if (!isInterceptor){
                    if (requestBody != null) {
                        val buffer = Buffer()
                        requestBody.writeTo(buffer)
                        val oldParamsJson = buffer.readUtf8()
                        request = request.newBuilder()
                            .post(RequestBody.create(requestBody.contentType(), encode(oldParamsJson)))
                            .build()

                    }
                }
            }


        }


        return chain.proceed(request)
    }
}