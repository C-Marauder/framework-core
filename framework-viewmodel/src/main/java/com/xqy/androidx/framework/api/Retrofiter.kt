package com.xqy.androidx.framework.api

import android.os.Environment
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.xqy.androidx.framework.BuildConfig
import com.xqy.androidx.framework.interceptor.ExceptionInterceptor
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *@desc
 *@creator 小灰灰
 *@Time 2019-06-23 - 01:29
 **/
class Retrofiter private constructor(private val builder: Builder) {
    companion object {
        private const val TIME_OUT: Long = 8
        lateinit var mInstance: Retrofiter
        fun initModuleRetrofit(retrofiter: Retrofiter) {
            mInstance = retrofiter
        }

        fun builder(init: Builder.() -> Unit) {
            mInstance = Builder().apply(init).build()

        }

    }

    var mOkHttpClient: OkHttpClient
    var apiService: Any? = null
    val mApiServiceClz: Class<*> by lazy {
        apiService!!::class.java
    }
    private lateinit var mHttpSystemException: HttpSystemException
    fun catchHttpSystemException(httpSystemException: HttpSystemException) {
        this.mHttpSystemException = httpSystemException
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getService(): T {
        return apiService as T
    }

    init {
        mOkHttpClient = OkHttpClient.Builder()
            .apply {
                addInterceptor(
                    ExceptionInterceptor { code, path, msg ->
                        if (::mHttpSystemException.isInitialized) {
                            mHttpSystemException.invoke(code, path, msg)
                        }
                    }
                )
                if (!builder.interceptors.isNullOrEmpty()) {
                    builder.interceptors!!.forEach {
                        this.addInterceptor(it)
                    }
                }
                if (BuildConfig.DEBUG) {
                    this.addInterceptor(StethoInterceptor())
                }
            }
            .cache(builder.cache ?: Cache(Environment.getExternalStorageDirectory(), 10 * 1024 * 1024))
            .connectTimeout(builder.connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(builder.readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(builder.writeTimeout, TimeUnit.MILLISECONDS)
            .build()

        apiService = Retrofit.Builder()
            .baseUrl(builder.baseUrl)
            .client(builder.okHttpClient ?: mOkHttpClient)
            .apply {
                builder.callAdapterFactory?.let {
                    addCallAdapterFactory(it)
                }
            }
            .addConverterFactory(builder.converterFactory ?: GsonConverterFactory.create())
            .build()
            .create(builder.service)!!

    }

    class Builder {

        internal var okHttpClient: OkHttpClient? = null
        internal var cache: Cache? = null
        internal var connectTimeout: Long = TIME_OUT
        internal var readTimeout: Long = TIME_OUT
        internal var writeTimeout: Long = TIME_OUT
        internal lateinit var baseUrl: String
        internal lateinit var service: Class<*>
        internal var converterFactory: Converter.Factory? = null
        internal var callAdapterFactory: CallAdapter.Factory? = null
        internal var interceptors: MutableList<Interceptor>? = null
        fun connectTimeout(init: () -> Long) {
            connectTimeout = init()
        }

        fun readTimeout(init: () -> Long) {
            readTimeout = init()
        }

        fun writeTimeout(init: () -> Long) {
            writeTimeout = init()
        }

        fun newOkHttpClient(init: () -> OkHttpClient) {
            okHttpClient = init()
        }

        fun baseUrl(init: () -> String) {
            baseUrl = init()
        }


        fun newConverterFactory(init: () -> Converter.Factory) {
            converterFactory = init()
        }

        fun newCallAdapterFactory(init: () -> CallAdapter.Factory) {
            callAdapterFactory = init()
        }

        fun interceptors(init: () -> MutableList<Interceptor>) {
            interceptors = init()
        }

        fun apiService(init: () -> Class<*>) {
            service = init()
        }

        fun build() = Retrofiter(this)
    }
}

typealias HttpSystemException = (code: Int, path: String, msg: String) -> Unit