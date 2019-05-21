package com.androidx.frameworkcore.viewmodel.repository

import androidx.lifecycle.LiveData

interface DBCallback<T> {
    fun shouldFetch(result: T?):Boolean
    fun loadFromDB():LiveData<T?>
    fun saveResult( result: T)
}