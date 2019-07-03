package com.xqy.androidx.framework.adapter
import android.util.Log
import com.xqy.androidx.framework.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

/**
 *@desc
 *@creator 小灰灰
 *@Time 2019-05-25 - 14:09
 **/
internal class ApiResponseAdapter<R>(private val responseType: Type) : CallAdapter<R, ApiResponse<R>> {
    override fun adapt(call: Call<R>): ApiResponse<R> {
        var apiResponse:ApiResponse<R>?=null
        call.enqueue(object :Callback<R>{
            override fun onFailure(call: Call<R>, t: Throwable) {
                Log.e("===>","${t.message}")
                apiResponse = ApiResponse.create<R>(t.message!!)
            }

            override fun onResponse(call: Call<R>, response: Response<R>) {
                val body = response.body()
                apiResponse= if (body == null) {
                    ApiResponse.create<R>(response.message())
                } else {
                    ApiResponse.create<R>(body)
                }
            }

        })

        return apiResponse!!
    }

    override fun responseType(): Type = responseType
}