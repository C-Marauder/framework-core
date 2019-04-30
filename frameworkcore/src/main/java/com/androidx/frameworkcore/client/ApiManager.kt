package com.androidx.frameworkcore.client

import android.os.Environment
import com.androidx.frameworkcore.BuildConfig
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal class ApiManager<T> (
    private val service:Class<T>,
    private val baseUrl: String,
    private val interceptors: MutableList<Interceptor>? = null) {
    companion object {
        private const val TIME_OUT: Long = 30
    }

    private val mCache: Cache by lazy {
        Cache(Environment.getExternalStorageDirectory(), 10 * 1024 * 1024)
    }
     var mOkHttpClient: OkHttpClient
     var apiService:T
    init {
        mOkHttpClient = OkHttpClient.Builder()
            .apply {
                if (!interceptors.isNullOrEmpty()) {
                    interceptors.forEach {
                        this.addInterceptor(it)
                    }
                }
                if (BuildConfig.DEBUG){
                    this.addInterceptor(StethoInterceptor())
                }
            }

            .cache(mCache)
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