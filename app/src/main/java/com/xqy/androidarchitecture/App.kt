package com.xqy.androidarchitecture

import android.app.Application
import com.androidx.frameworkcore.application.AndroidApplication
import kotlin.properties.Delegates

class App:Application() {
    companion object {
        var instance by Delegates.notNull<App>()
        private val httpUrl:String ="http://gank.io/api/xiandu/"

    }

    override fun onCreate() {
        super.onCreate()
        instance=this
        //DatabaseManager.initRealm(this)
        //val dog = Dog::class
        AndroidApplication.run(application = this,baseUrl = httpUrl)
    }
}