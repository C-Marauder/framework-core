package com.androidx.frameworkcore.application

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.androidx.frameworkcore.module.AndroidModule
import com.androidx.frameworkcore.logic.AppView
import com.androidx.frameworkcore.module.AppModule
import com.xqy.androidx.framework.api.Retrofiter
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object AndroidApplication : Application.ActivityLifecycleCallbacks {
    private val mAndroidModules: MutableList<AndroidModule> by lazy {
        mutableListOf<AndroidModule>()
    }
    private lateinit var mAppModule: AppModule
    fun run(
        vararg childModules: KClass<out AndroidModule>,
        application: Application,
        baseUrl: String,
        retrofiter: Retrofiter? = null
    ) {

        Retrofiter.builder {
            baseUrl {
                baseUrl
            }

            apiService {
                Class.forName("com.xqy.androidx.framework.service.AndroidService")
            }

        }
        mAppModule = AppModule()
        if (!childModules.isNullOrEmpty()) {
            childModules.forEach {
                it.createInstance().apply {
                    initAndroidModule(Retrofiter.mInstance)
                    mAndroidModules.add(this)
                }
            }
        }


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
            if (activity is AppView) {
                mAndroidModules.forEach {
                    it.bind(activity)
                }
            }
            if (a is AppCompatActivity) {
                a.supportFragmentManager.registerFragmentLifecycleCallbacks(object :
                    FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                        super.onFragmentPreCreated(fm, f, savedInstanceState)
                        if (f is AppView){
                            mAppModule.bind(f)
                        }
                    }

                }, true)
            }

        }
    }

}