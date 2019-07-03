package com.xqy.androidx.framework.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xqy.androidx.framework.AppViewModel
import com.xqy.androidx.framework.Status

/**
 *@desc 工具文件
 *@creator 小灰灰
 *@Time 2019-07-01 - 09:20
 **/
typealias UnConnected = ()->Unit
typealias OnLoading = ()->Unit
typealias OnError=(msg:String)->Unit


 fun<Params,Result> LifecycleOwner.execute(service:String,
                                          params: Params?=null,
                                          unConnected: UnConnected?=null,
                                          onLoading:OnLoading?=null,
                                          onError:OnError?=null,
                                          onSuccess: (result:Result?)->Unit){
    val appViewModel = when(this) {
         is AppCompatActivity -> ViewModelProviders.of(this)[AppViewModel::class.java]
         is Fragment -> ViewModelProviders.of(this)[AppViewModel::class.java]
        else -> {
            throw IllegalArgumentException("the LifecycleOwner's child must be AppCompatActivity or Fragment")
        }
    }

    val liveData = appViewModel.request<Params,Result>(service,params){
        unConnected?.invoke()
    }
    liveData.observe(this, Observer {resource->
        when (resource.status) {
            Status.UNCONNECTED-> unConnected?.invoke()
            Status.LOADING -> onLoading?.invoke()
            Status.SUCCESS -> onSuccess(resource.data)
            Status.ERROR -> onError?.invoke(resource.message!!)
        }
    })

}

internal const val PKG:String = "com.xqy.androidx.framework"
const val HTTP_OK:String ="200"