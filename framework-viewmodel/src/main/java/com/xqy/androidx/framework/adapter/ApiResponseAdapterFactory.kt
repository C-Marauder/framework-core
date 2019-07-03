package com.xqy.androidx.framework.adapter
import com.xqy.androidx.framework.ApiResponse
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 *@desc
 *@creator 小灰灰
 *@Time 2019-05-25 - 14:17
 **/
internal class ApiResponseAdapterFactory: CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        val mClz = getRawType(returnType)
        if (mClz != ApiResponse::class.java){
            return null
        }
        if (returnType !is ParameterizedType){
            return null
        }
       val observerType = getParameterUpperBound(0, returnType)

        return ApiResponseAdapter<Any>(observerType)
    }
}