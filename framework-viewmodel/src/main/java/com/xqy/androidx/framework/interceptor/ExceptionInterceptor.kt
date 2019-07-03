package com.xqy.androidx.framework.interceptor
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody

/**
 * 异常处理拦截器
 */
internal class ExceptionInterceptor(private val catchException:(code:Int,path:String,msg:String)->Unit) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        val path = request.url().encodedPath()
        val body = response.body()
        val code = response.code()
        if (response.isSuccessful) {
            if (body == null) {
                catchException(code,path,response.message())
            } else {
                val content = body.string()
                val mediaType = body.contentType()
                response = response.newBuilder()
                    .body(ResponseBody.create(mediaType, content))
                    .build()
            }

        } else {
            catchException(code ,path,response.message() )
        }

        return response
    }
}