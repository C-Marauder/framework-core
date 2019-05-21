package com.androidx.frameworkcore

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import com.androidx.frameworkcore.app.AndroidApplication
import com.androidx.frameworkcore.http.interceptor.exception.HttpException
import com.androidx.frameworkcore.http.state.Status
import com.androidx.frameworkcore.mvi.AppView
import com.androidx.frameworkcore.viewmodel.AppViewModel

private lateinit var mConnectivityManager: ConnectivityManager
private fun isConnected(application: Application) :Boolean{
    if (!::mConnectivityManager.isInitialized) {
        mConnectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    val activeNetworkInfo = mConnectivityManager.activeNetworkInfo
    return !(activeNetworkInfo == null || !activeNetworkInfo.isConnected)
}

suspend fun <Params, Result> AppView.execute(
    repositoryName: String,
    params: Params? = null,
    onLoading: () -> Unit,
    onSuccess: (result: Result?) -> Unit,
    onError: (msg: String) -> Unit
) {
    val isConnected:Boolean
    val mViewModel = when (this) {
        is AppCompatActivity -> {
            isConnected = isConnected(this.application)
            ViewModelProviders.of(this)[AppViewModel::class.java]
        }
        is Fragment -> {
            isConnected = isConnected(this.activity!!.application)
            ViewModelProviders.of(this)[AppViewModel::class.java]
        }
        else -> {
            throw Exception("the class who implementation AppView is not AppCompatActivity or Fragment")
        }
    }
    if (!isConnected){
        onError("no network connected")
        return
    }
    mViewModel.execute<Params, Result>(repositoryName, params).observe(this as LifecycleOwner) { resource ->
        when (resource.status) {
            Status.LOADING -> onLoading()
            Status.SUCCESS -> onSuccess(resource.data)
            Status.ERROR -> onError(resource.message!!)
        }
    }
}

 fun  LifecycleOwner.catchHttpException(catch:(httpException:HttpException)->Unit){
    AndroidApplication.httpExceptionObserver.observe(this){
        catch(it)
    }
}