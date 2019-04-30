package com.androidx.frameworkcore

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.androidx.frameworkcore.http.state.Status
import com.androidx.frameworkcore.mvi.AppView
import com.androidx.frameworkcore.viewmodel.AppViewModel

val String?.errorMsg: String
    get() = if (this.isNullOrEmpty()) {
        "unKnow error"
    } else {
        this
    }

typealias OnLoading = () -> Unit
typealias OnError = (message: String) -> Unit
typealias OnSuccess<ResultType> = (data: ResultType) -> Unit

fun <RequestType, ResultType> AppView.request(
    repositoryName: String,
    params: RequestType,
    onLoading: OnLoading,
    onError: OnError,
    onSuccess: OnSuccess<ResultType>
) {
    val mViewModel = when (this) {
        is AppCompatActivity -> ViewModelProviders.of(this)[AppViewModel::class.java]
        is Fragment -> ViewModelProviders.of(this)[AppViewModel::class.java]
        else -> {
            throw Exception("the implementation class of AppView is not AppCompatActivity or Fragment")
        }
    }
    mViewModel.request<RequestType, ResultType>(repositoryName, params)?.let {
        it.observe(this as LifecycleOwner, Observer { resource ->
            when (resource.status) {
                Status.LOADING -> onLoading.invoke()
                Status.SUCCESS -> onSuccess.invoke(resource.data!!)
                Status.ERROR -> onError.invoke(resource.message!!)
            }

        })
    }

}