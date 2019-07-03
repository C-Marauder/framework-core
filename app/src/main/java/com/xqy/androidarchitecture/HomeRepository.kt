package com.xqy.androidarchitecture

import com.androidx.annotation.Service
import com.xqy.androidx.framework.AbsRepository
import com.xqy.androidx.framework.ApiResponse
import retrofit2.Call

@Service("getThings")
class HomeRepository: AbsRepository<String, MutableList<String>>() {


}