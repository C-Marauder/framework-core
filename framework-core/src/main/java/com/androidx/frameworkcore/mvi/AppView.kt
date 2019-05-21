package com.androidx.frameworkcore.mvi

import androidx.lifecycle.MutableLiveData


interface AppView {
    val mFragmentLifecycle:MutableLiveData<Int>? get() =null
}