package com.xqy.androidx.framework

/**
 *@desc
 *@creator 小灰灰
 *@Time 2019-06-23 - 00:56
 **/

enum class Status{
    SUCCESS, ERROR, LOADING,UNCONNECTED
}


data class Resource<T> (val status: Status, val data: T?, val message:String?){
    companion object {
        fun <T>unConnected():Resource<T>{
            return Resource(Status.UNCONNECTED,null,null)
        }
        fun <T>success(data:T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }
        fun <T> empty():Resource<T> = Resource(Status.SUCCESS,null,null)
        fun <T> error(msg: String): Resource<T> {
            return Resource(Status.ERROR, null, msg)
        }

        fun <T> loading(): Resource<T> {
            return Resource(Status.LOADING, null, null)
        }
    }
}

sealed class ApiResponse<T> {

    companion object {
        fun <T> create(msg: String): ApiErrorResponse<T> {
            return ApiErrorResponse(msg)
        }

        fun <T> create(data: T): ApiSuccessResponse<T> {
            return ApiSuccessResponse(data)
        }

        fun <T> create(): ApiEmptyResponse<T> {
            return ApiEmptyResponse()
        }

    }
}

data class ApiErrorResponse<T>(val msg: String) : ApiResponse<T>()
data class ApiSuccessResponse<T>(val data: T) : ApiResponse<T>()
open class ApiEmptyResponse<T>:ApiResponse<T>()