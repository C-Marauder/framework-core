package com.androidx.frameworkcore.viewmodel.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.androidx.frameworkcore.app.AndroidApplication
import com.androidx.frameworkcore.http.state.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import retrofit2.Call

abstract class AbsRepository<Service, Params, Result> {

    abstract fun execute(service: Service, params: Params?): Call<Result>

    @Suppress("UNCHECKED_CAST")
    internal fun execute(params: Params?): LiveData<Resource<Result>> = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        var disposable: DisposableHandle? = null
        var dbCallback: DBCallback<Result>? = null
        if (this@AbsRepository is DBCallback<*>) {
            dbCallback = this@AbsRepository as DBCallback<Result>
            disposable = emitSource(loadFromDB().map {
                Resource.loading<Result>()
            })
        }
        try {
            with(execute(AndroidApplication.getApiService(), params).execute()) {
                if (isSuccessful && code() == 200) {
                    val body = body()
                    if (disposable != null) {
                        disposable.dispose()
                        if (dbCallback!!.shouldFetch(body)) {
                            dbCallback.saveResult(body!!)
                        }
                        emitSource(dbCallback.loadFromDB().map {
                            Resource.success(body)
                        })
                    } else {
                        emit(Resource.success(body))
                    }

                } else {
                    disposable?.dispose()
                    emit(Resource.error(message()))
                }
            }
        } catch (e: Exception) {
            disposable?.dispose()
            emit(Resource.error(e.message ?: "unKnow error"))
        }

    }


}