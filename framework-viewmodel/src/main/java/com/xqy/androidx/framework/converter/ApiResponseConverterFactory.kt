//package com.xqy.androidx.framework.converter
//
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import com.xqy.androidx.framework.converter.ApiRequestBodyConverter
//import com.xqy.androidx.framework.model.ApiKeyModel
//import okhttp3.RequestBody
//import okhttp3.ResponseBody
//import retrofit2.Converter
//import retrofit2.Retrofit
//import java.lang.reflect.Type
//
///**
// *@desc
// *@creator 小灰灰
// *@Time 2019-05-25 - 17:43
// **/
//internal class ApiResponseConverterFactory(private val apiKeyModel: ApiKeyModel):Converter.Factory() {
//    override fun requestBodyConverter(
//        type: Type,
//        parameterAnnotations: Array<Annotation>,
//        methodAnnotations: Array<Annotation>,
//        retrofit: Retrofit
//    ): Converter<*, RequestBody>? {
//        val adapter = gson.getAdapter(TypeToken.get(type))
//
//        return ApiRequestBodyConverter(gson, adapter)
//    }
//    private val gson: Gson by lazy {
//        Gson()
//    }
//    override fun responseBodyConverter(
//        type: Type,
//        annotations: Array<Annotation>,
//        retrofit: Retrofit
//    ): Converter<ResponseBody, *>? {
//        val adapter = gson.getAdapter(TypeToken.get(type))
//        return ApiResponseConverter(apiKeyModel,gson, adapter)
//    }
//}