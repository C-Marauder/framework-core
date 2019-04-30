package com.androidx.frameworkcore.viewmodel.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.androidx.frameworkcore.app.AndroidApplication
import com.androidx.frameworkcore.errorMsg
import com.androidx.frameworkcore.http.state.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class AppRepository<ApiService, RequestType, ResultType> {
    private val result: MediatorLiveData<Resource<ResultType>> by lazy {
        MediatorLiveData<Resource<ResultType>>()
    }

    private val apiResource: MutableLiveData<ApiResponse<ResultType>>by lazy {
        MutableLiveData<ApiResponse<ResultType>>()
    }

    /**
     * 从网络获取数据
     */
    abstract fun requestFromNetwork(apiService: ApiService, requestType: RequestType): Call<ResultType>

    fun execute(requestType: RequestType): LiveData<Resource<ResultType>> {
        result.value = Resource.loading()
        GlobalScope.async(Dispatchers.Main) {
            try {
                if (this is DataBaseCallback<*>) {
                    val repository = this as DataBaseCallback<ResultType>
                    val deferred = async {
                        repository.loadFromDB()
                    }
                    val dbResult = deferred.await()
                    result.addSource(dbResult) {
                        result.removeSource(dbResult)
                        if (repository.shouldFetch(it)) {
                            loadFromNetwork(dbResult, requestType)
                        } else {

                            result.addSource(dbResult) { newData ->
                                setValue(Resource.success(newData))
                            }
                        }

                    }
                } else {
                    loadFromNetwork(null, requestType)
                }
            } catch (e: Exception) {
                result.value = Resource.error(e.message.errorMsg)
            }

        }.start()
        return result
    }

    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun loadFromNetwork(dbResult: LiveData<ResultType?>? = null, requestType: RequestType) {

        val apiResponse = requestFromNetwork(AndroidApplication.getApiService(), requestType)

        if (dbResult != null) {
            result.addSource(dbResult) {
                setValue(Resource.loading())
            }
        }
        result.addSource(apiResource) {
            result.removeSource(apiResource)
            dbResult?.let {
                result.removeSource(dbResult)

            }
            when (it) {
                is ApiSuccessResponse -> {
                    if (this is DataBaseCallback<*>) {
                        this as DataBaseCallback<ResultType>
                        this.saveResult(it.data)
                        val dbResource = loadFromDB()
                        result.addSource(dbResource) { dbResult ->
                            setValue(Resource.success(dbResult))
                        }

                    }

                }
                is ApiEmptyResponse -> {
                    setValue(Resource.success(null))
                }

                is ApiErrorResponse -> {
                    setValue(Resource.error(it.errorMsg))
                }
            }
        }

        apiResponse.enqueue(object : Callback<ResultType> {
            override fun onFailure(call: Call<ResultType>, t: Throwable) {
                apiResource.value = ApiResponse.create(t.message.errorMsg)

            }

            override fun onResponse(call: Call<ResultType>, response: Response<ResultType>) {
                if (response.isSuccessful && response.code() == 200) {
                    val body = response.body()
                    apiResource.value = if (body == null) {
                        ApiResponse.create()
                    } else {
                        ApiResponse.create(body)
                    }

                } else {
                    apiResource.value = ApiResponse.create(response.message())
                }
            }

        })
    }


}