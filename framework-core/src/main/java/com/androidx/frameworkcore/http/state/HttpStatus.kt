package com.androidx.frameworkcore.http.state

enum class Status{
    SUCCESS, ERROR, LOADING
}


data class Resource<T> (val status: Status, val data: T?, val message:String?){
    companion object {
        fun <T>success(data:T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

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

data class ApiErrorResponse<T>(val errorMsg: String) : ApiResponse<T>()
open class ApiSuccessResponse<T>(val data: T) : ApiResponse<T>()
class ApiEmptyResponse<T> : ApiResponse<T>()




