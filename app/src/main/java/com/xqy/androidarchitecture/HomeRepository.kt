package com.xqy.androidarchitecture

import com.androidx.frameworkcore.viewmodel.repository.AbsRepository
import com.androidx.processor.Service
import retrofit2.Call

@Service("getThings")
class HomeRepository: AbsRepository<ApiService,String,String>() {
    override fun execute(service: ApiService, params: String?): Call<String> {
        return service.getThings()

    }

}