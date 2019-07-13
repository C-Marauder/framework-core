package com.androidx.frameworkcore.module

import com.androidx.frameworkcore.logic.AppView
import com.androidx.frameworkcore.logic.PresenterManager
import com.xqy.androidx.framework.api.Retrofiter
import com.xqy.androidx.framework.module.IModule
import kotlin.reflect.full.primaryConstructor

/**
 *@desc
 *@creator 小灰灰
 *@Time 2019-06-30 - 22:02
 **/
interface AndroidModule:IModule {
    fun initAndroidModule(retrofiter: Retrofiter?){
        initModule(retrofiter, PresenterManager.mPresenterMap)
    }

    fun bind(mAppView: AppView){
        PresenterManager.mPresenterMap.entries.filter {
            it.value == mAppView::class.simpleName!!
        }.forEach {
            it.key.primaryConstructor!!.call(mAppView)
        }
    }
}