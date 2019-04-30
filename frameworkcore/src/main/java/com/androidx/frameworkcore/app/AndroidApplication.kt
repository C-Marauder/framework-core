package com.androidx.frameworkcore.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.androidx.frameworkcore.client.ApiManager
import com.androidx.frameworkcore.mvi.AppPresenter
import com.androidx.frameworkcore.mvi.AppView
import com.androidx.frameworkcore.viewmodel.repository.AppRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.HashMap
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object AndroidApplication : Application.ActivityLifecycleCallbacks {

    private lateinit var mApiManager: ApiManager<*>

    @Suppress("UNCHECKED_CAST")
    internal fun <T>getApiService():T{
        if (!AndroidApplication::mApiManager.isInitialized){

            throw Exception("please init ApiManager before use it")
        }
        return mApiManager.apiService as T
    }

    fun getOkHttpClient():OkHttpClient{
        if (!AndroidApplication::mApiManager.isInitialized){

            throw Exception("please init ApiManager before use it")
        }
        return mApiManager.mOkHttpClient
    }
    internal val mRepositoryHashMap: ConcurrentHashMap<String, KClass<out AppRepository<*, *, *>>> by lazy {
        ConcurrentHashMap<String, KClass<out AppRepository<*, *, *>>>()
    }


    private val mPresenterListHashMap: HashMap<String, MutableList<KClass<out AppPresenter<out AppView>>>> by lazy {
        hashMapOf<String,MutableList<KClass<out AppPresenter<out AppView>>>>()

    }

    fun run (application: Application, service:Class<*>, baseUrl:String, interceptors:MutableList<Interceptor>?=null ){
        val mFrameworkManager= Class.forName("com.androidx.frameworkcore.FrameworkManager").newInstance()
        mFrameworkManager::class.java.getDeclaredMethod("initFramework",ConcurrentHashMap::class.java,HashMap::class.java).invoke(mFrameworkManager,
            mRepositoryHashMap,mPresenterListHashMap)
        mApiManager = ApiManager(service,baseUrl,interceptors)
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
        activity?.let {a->
            if (a is AppView){
                bindPresenterToUI(a)
            }
            if (a is AppCompatActivity){
                a.supportFragmentManager.registerFragmentLifecycleCallbacks(object :
                    FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                        super.onFragmentCreated(fm, f, savedInstanceState)
                        bindPresenterToUI(f)
                    }

                },true)
            }

        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun bindPresenterToUI(ui:Any){
        val mPresenters = mPresenterListHashMap[ui::class.simpleName!!]
        mPresenters?.forEach {
            val presenter = it.primaryConstructor!!.call(ui) as AppPresenter<AppView>
            if (ui is LifecycleOwner){
                ui.lifecycle.addObserver(presenter)
            }

        }
    }
}