package com.xqy.androidarchitecture

import androidx.lifecycle.LiveData
import com.androidx.processor.Service
import com.androidx.frameworkcore.viewmodel.repository.AppRepository
import com.androidx.frameworkcore.viewmodel.repository.DataBaseCallback
import retrofit2.Call

@Service("getThings")
class HomeRepository: AppRepository<ApiService,String,String>() {

    override fun requestFromNetwork(apiService: ApiService, requestType: String): Call<String> {
        return apiService.getThings(requestType)
    }
}