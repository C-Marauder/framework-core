package com.androidx.frameworkcore.http.interceptor

import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody

/**
 * 异常处理拦截器
 */
internal class ExceptionInterceptor(private val codeKey:String,
                                    private val msgKey:String,
                                    private val handle:(code:String,msg:String,path:String)->Unit):Interceptor {
    private val jsonParser:JsonParser by lazy {
        JsonParser()
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val request  = chain.request()
        var response = chain.proceed(request)
        val path = request.url().encodedPath()
        val body = response.body()

        if (body == null){
            handle("404","responseBody is null",path)
        }else{
            val realCode = response.code()
            if (realCode == 200){
                val content = body.string()
                val jsonBody = jsonParser.parse(content).asJsonObject

                val jsonKeys = jsonBody.entrySet()
                val codeValue = jsonKeys.find {
                    it.key == codeKey
                }
                val messageValue = jsonKeys.find {
                    it.key == msgKey
                }
                val code = codeValue?.value?.asString ?: "404"
                val msg = messageValue?.value?.asString?:"unKnow Error"
                val mediaType = body.contentType()
                response = response.newBuilder()
                    .body(ResponseBody.create(mediaType,content))
                    .build()
                handle(code,msg,path)
            }else{
                handle("$realCode",response.message(),path)
            }

        }


        return response
    }
}