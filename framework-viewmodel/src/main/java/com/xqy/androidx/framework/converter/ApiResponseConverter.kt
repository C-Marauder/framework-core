//package com.xqy.androidx.framework.converter
//import com.google.gson.Gson
//import com.google.gson.JsonIOException
//import com.google.gson.JsonParser
//import com.google.gson.TypeAdapter
//import com.google.gson.stream.JsonToken
//import com.xqy.androidx.framework.ApiResponse
//import com.xqy.androidx.framework.model.ApiKeyModel
//import com.xqy.androidx.framework.utils.HTTP_OK
//import okhttp3.ResponseBody
//import retrofit2.Converter
//
///**
// *@desc convert responseBody to ApiResponse
// *@creator 小灰灰
// *@Time 2019-05-25 - 16:55
// **/
//internal class ApiResponseConverter<T>(
//    private val apiKeyModel: ApiKeyModel,
//    private val gson: Gson,
//    private val adapter: TypeAdapter<T>
//) : Converter<ResponseBody, ApiResponse<T>> {
//    private val jsonParser: JsonParser by lazy {
//        JsonParser()
//    }
//
//    override fun convert(value: ResponseBody): ApiResponse<T>? {
//        val jsonReader = gson.newJsonReader(value.charStream())
//        val jsonElement = value.use { _ ->
//            val mJsonElement = jsonParser.parse(jsonReader)
//            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
//                throw JsonIOException("JSON document was not fully consumed.")
//            }
//
//            mJsonElement
//        }
//        try {
//            return if (jsonElement.isJsonObject) {
//                with(jsonElement.asJsonObject) {
//                    val codePair = apiKeyModel.pair
//                    val codeElement = get(codePair.first)
//                    val code = when(codePair.second){
//                        Boolean::class.java-> codeElement.asBoolean.toString()
//                        String::class.java->codeElement.asString
//                        Int::class.java->codeElement.asInt.toString()
//                        else->throw Exception("")
//                    }
//                    val msg = get(apiKeyModel.msgKey)?.asString
//                    val contentJsonElement = get(apiKeyModel.contentKey)
//                    val contentJson = when {
//                        contentJsonElement.isJsonObject -> contentJsonElement.asJsonObject
//                        contentJsonElement.isJsonArray -> contentJsonElement.asJsonArray
//                        else -> contentJsonElement.asJsonNull
//                    }
//
//                   val content = adapter.fromJson(contentJson.toString())
//                    ApiResponse.create(code,content,msg)
//
//                }
//            } else {
//                ApiResponse.create(HTTP_OK, null, "response body is not object")
//            }
//        } catch (e: Exception) {
//            return ApiResponse.create(HTTP_OK, null, e.message!!)
//
//
//        }
//
//
//    }
//}