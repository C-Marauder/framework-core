package com.xqy.androidx.framework

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.*
import com.xqy.androidx.framework.api.Retrofiter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 *@desc
 *@creator 小灰灰
 *@Time 2019-06-23 - 00:21
 **/

abstract class AbsRepository<Params, Result> {

    open fun handleResponse(result: Result): Resource<Result>? {
        return null
    }

    open fun interruptedResponse(params: Params?): ApiResponse<Result>? {
        return null
    }

    private val mResultLiveData: MutableLiveData<Resource<Result>> by lazy {
        MutableLiveData<Resource<Result>>()
    }

    @Suppress("UNCHECKED_CAST")
    fun request(isConnected: Boolean, service: String, params: Params?): LiveData<Resource<Result>> {
        var dbCallback: DatabaseCallback<Result>? = null
        var mApiResponse: ApiResponse<Result>? = null
        try {
            if (!isConnected) {
                mResultLiveData.value = Resource.unConnected<Result>()
            } else {
                mResultLiveData.value = Resource.loading<Result>()
                if (this@AbsRepository is DatabaseCallback<*>) {
                    dbCallback = this@AbsRepository as DatabaseCallback<Result>
                    val r = loadFromDB()
                    r?.let {
                        mResultLiveData.value = Resource.success<Result>(it)
                    }
                }
                val apiResponse = interruptedResponse(params)
                if (apiResponse == null) {
                    val call = if (params == null) {
                        Retrofiter.mInstance.mApiServiceClz.getDeclaredMethod(service)
                            .invoke(Retrofiter.mInstance.apiService)
                    } else {
                        Retrofiter.mInstance.mApiServiceClz.getDeclaredMethod(service, (params as Any)::class.java)
                            .invoke(Retrofiter.mInstance.apiService, params)

                    } as Call<Result>
                    call.enqueue(object : Callback<Result> {
                        override fun onFailure(call: Call<Result>, t: Throwable) {
                            mResultLiveData.value = Resource.error(t.message ?: t.localizedMessage)
                        }

                        override fun onResponse(call: Call<Result>, response: Response<Result>) {
                            val body = response.body()
                            mResultLiveData.value = if (response.isSuccessful) {
                                if (body == null) {
                                    Resource.empty()
                                } else {
                                    val resource = handleResponse(body) ?: Resource.success<Result>(body)
                                    if (dbCallback != null) {
                                        if (dbCallback.shouldFetch(resource.data)) {
                                            dbCallback.saveResult(resource.data!!)
                                        }
                                    }
                                    resource
                                }

                            } else {
                                Resource.error<Result>("responseBody is empty")
                            }

                        }

                    })
                } else {
                    with(apiResponse) {
                        mResultLiveData.value = when (this) {
                            is ApiSuccessResponse -> {
                                if (dbCallback != null) {
                                    if (dbCallback.shouldFetch(data)) {
                                        dbCallback.saveResult(data)
                                    }
                                }
                                Resource.success<Result>(data)

                            }
                            is ApiEmptyResponse -> Resource.empty()

                            is ApiErrorResponse -> Resource.error(msg)

                            else -> Resource.empty()
                        }
                    }
                }


            }

        } catch (e: Exception) {
            mResultLiveData.value = Resource.error<Result>(e.message ?: "unknown error")
        }

        return mResultLiveData

    }


}


internal class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepositories: ConcurrentHashMap<String, AbsRepository<*, *>> by lazy {
        ConcurrentHashMap<String, AbsRepository<*, *>>()
    }

    private val mConnectivityManager: ConnectivityManager by lazy {
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun <Params, Result> request(service: String, params: Params?, noNetWork: () -> Unit): LiveData<Resource<Result>> {

        val connected = mConnectivityManager.activeNetworkInfo.isConnected
        var mCacheRepository = mRepositories[service]
        if (mCacheRepository == null) {
            val mRepository =
                ServiceManager.mServiceMap[service] ?: throw NullPointerException("there is no $service service")
            mCacheRepository = mRepository.primaryConstructor!!.call()
            mRepositories[service] = mCacheRepository
        }

        return (mCacheRepository as AbsRepository<Params, Result>).request(connected, service, params)


    }
}

internal object ServiceManager {
    val mServiceMap: ConcurrentHashMap<String, KClass<AbsRepository<*, *>>> by lazy {
        ConcurrentHashMap<String, KClass<AbsRepository<*, *>>>()
    }
}
