package com.androidx.frameworkcore.mvi

import androidx.lifecycle.MutableLiveData

interface AppView {

    val isObserverViewCreated:MutableLiveData<Boolean>?get() = null
}