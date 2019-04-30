package com.xqy.androidarchitecture

import android.app.Application
import com.androidx.frameworkcore.app.AndroidApplication
import kotlin.properties.Delegates

class App:Application() {
    companion object {
        var app by Delegates.notNull<App>()
        private val httpUrl:String ="http://v.juhe.cn/movie/"

    }

    override fun onCreate() {
        super.onCreate()
        app=this
        AndroidApplication.run(this,ApiService::class.java, httpUrl)
        //DatabaseManager.initRealm(this)
        //val dog = Dog::class

    }
}