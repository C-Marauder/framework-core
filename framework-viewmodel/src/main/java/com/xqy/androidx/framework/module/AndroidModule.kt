package com.xqy.androidx.framework.module

import android.util.Log
import com.xqy.androidx.framework.ServiceManager
import com.xqy.androidx.framework.api.Retrofiter
import com.xqy.androidx.framework.utils.PKG
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 *@desc
 *@creator 小灰灰
 *@Time 2019-06-30 - 21:54
 **/
interface IModule {

    fun initModule(retrofiter: Retrofiter?, map: ConcurrentHashMap<*, String>) {
        val moduleName = this::class.java.simpleName
       val clzName = if (moduleName == "AppModule"){
            PKG
        }else{
           "$PKG.$moduleName"
       }
        val mFrameworkManager = Class.forName("$clzName.FrameworkManager")
        val constructor = mFrameworkManager.getDeclaredConstructor(ConcurrentHashMap::class.java, ConcurrentHashMap::class.java)
        constructor.isAccessible = true
        constructor.newInstance(ServiceManager.mServiceMap, map)
        retrofiter?.let {
            Retrofiter.mInstance = it

        }

    }
}