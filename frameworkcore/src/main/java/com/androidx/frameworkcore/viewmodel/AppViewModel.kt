package com.androidx.frameworkcore.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.androidx.frameworkcore.R
import com.androidx.frameworkcore.viewmodel.repository.AppRepository
import com.androidx.frameworkcore.app.AndroidApplication
import com.androidx.frameworkcore.http.state.Resource
import kotlin.reflect.full.createInstance

internal class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val mConnectivityManager: ConnectivityManager by lazy {
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    private var mApplication: Application = getApplication()
    fun <RequestType, ResultType> request(
        repositoryName: String,
        params: RequestType): LiveData<Resource<ResultType>>? {
        val activeNetworkInfo = mConnectivityManager.activeNetworkInfo
        return if (activeNetworkInfo == null || !activeNetworkInfo.isConnected) {
            Toast.makeText(mApplication, mApplication.getString(R.string.unconnected_network), Toast.LENGTH_LONG).show()
            null
        } else {
            val repository = AndroidApplication.mRepositoryHashMap[repositoryName]?.createInstance() as AppRepository<*, RequestType, ResultType>
            repository.execute(params)
        }
    }

}