package com.xqy.androidarchitecture

import com.androidx.processor.Service
import com.androidx.frameworkcore.viewmodel.repository.AppRepository
import retrofit2.Call

@Service("getThings")
class HomeRepository: AppRepository<ApiService,String,String>() {
    override fun requestFromNetwork(apiService: ApiService, requestType: String): Call<String> {
        return apiService.getThings(requestType)
    }


//    override fun  requestFromNetwork(requestType: City): Call<String> {
//        return App.apiService.getThings(requestType)
//    }


}