package com.androidx.frameworkcore.http.client

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.androidx.frameworkcore.BuildConfig
import com.androidx.frameworkcore.http.interceptor.exception.ExceptionInterceptor
import com.androidx.frameworkcore.http.interceptor.exception.HttpException
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal class ApiClient<T>(
    private val service: Class<T>,
    private val baseUrl: String,
    private val mHttpExceptionLiveData: MutableLiveData<HttpException>,
    private val interceptors: MutableList<Interceptor>? = null) {
    companion object {
        private const val TIME_OUT: Long = 8
    }

    var mOkHttpClient: OkHttpClient
    var apiService: T

    init {
        mOkHttpClient = OkHttpClient.Builder()
            .apply {
                addInterceptor(
                    ExceptionInterceptor(mHttpExceptionLiveData)
                )
                if (!interceptors.isNullOrEmpty()) {
                    interceptors.forEach {
                        this.addInterceptor(it)
                    }
                }
                if (BuildConfig.DEBUG) {
                    this.addInterceptor(StethoInterceptor())
                }
            }
            .cache(Cache(Environment.getExternalStorageDirectory(), 10 * 1024 * 1024))
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .build()

        apiService = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(mOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(service)
    }


}