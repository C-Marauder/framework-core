package com.androidx.frameworkcore.app
import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import com.androidx.frameworkcore.http.client.ApiClient
import com.androidx.frameworkcore.http.interceptor.exception.HttpException
import com.androidx.frameworkcore.mvi.*
import com.androidx.frameworkcore.viewmodel.repository.AbsRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.HashMap
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object AndroidApplication : Application.ActivityLifecycleCallbacks {

    private lateinit var mApiClient: ApiClient<*>

    @Suppress("UNCHECKED_CAST")
    internal fun <T> getApiService(): T {
        if (!AndroidApplication::mApiClient.isInitialized) {

            throw Exception("please init ApiClient before use it")
        }
        return mApiClient.apiService as T
    }

    fun getOkHttpClient(): OkHttpClient {
        if (!AndroidApplication::mApiClient.isInitialized) {

            throw Exception("please init ApiClient before use it")
        }
        return mApiClient.mOkHttpClient
    }

    internal val mRepositoryHashMap: ConcurrentHashMap<String, KClass<out AbsRepository<*, *, *>>> by lazy {
        ConcurrentHashMap<String, KClass<out AbsRepository<*, *, *>>>()
    }


    private val mPresenterMap: HashMap<KClass<out AppPresenter<out AppView>>, String> by lazy {
        hashMapOf<KClass<out AppPresenter<out AppView>>, String>()
    }

    internal val httpExceptionObserver:MutableLiveData<HttpException> by lazy {
        MutableLiveData<HttpException>()
    }
    fun run(
        application: Application,
        service: Class<*>,
        baseUrl: String,
        interceptors: MutableList<Interceptor>? = null) {
        val mFrameworkManager = Class.forName("com.androidx.frameworkcore.FrameworkManager").newInstance()
        mFrameworkManager::class.java.getDeclaredMethod(
            "initFramework",
            ConcurrentHashMap::class.java,
            HashMap::class.java
        ).invoke(mFrameworkManager, mRepositoryHashMap, mPresenterMap)
        mApiClient = ApiClient(service, baseUrl, httpExceptionObserver, interceptors = interceptors)
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityPaused(activity: Activity?) {

    }

    override fun onActivityResumed(activity: Activity?) {

    }

    override fun onActivityStarted(activity: Activity?) {

    }

    override fun onActivityDestroyed(activity: Activity?) {

    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        activity?.let { a ->
            bindPresenterToUI(a)
            if (a is AppCompatActivity) {
                a.supportFragmentManager.registerFragmentLifecycleCallbacks(object :
                    FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                        super.onFragmentPreCreated(fm, f, savedInstanceState)
                        bindPresenterToUI(f)
                    }

                }, true)
            }

        }
    }

    private fun bindLifecycle(fragment: Fragment,lifecycleEvent: Int){
        if (fragment is AppView){
            fragment.mFragmentLifecycle?.postValue(lifecycleEvent)
        }
    }

    private fun bindPresenterToUI(ui: Any) {
        if (ui is AppView) {
            mPresenterMap.entries.filter {
                it.value == ui::class.simpleName!!
            }.forEach {
               it.key.primaryConstructor!!.call(ui)
            }
        }

    }
}