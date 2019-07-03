package com.xqy.androidarchitecture
import android.util.Log
import com.androidx.annotation.Contract
import com.androidx.frameworkcore.logic.AppPresenter
import com.androidx.frameworkcore.logic.AppView

@Contract(["MainActivity"])
class HomeContracts {

    interface HomeView: AppView {
    }

    class HomePresenter(mView: HomeView) : AppPresenter<HomeView>(mView) {
        override fun onUiInit() {
            Log.e("[=HomePresenter==","==onUiInit=>>>")

        }



        override fun onCreated() {
            super.onCreated()
            Log.e("[===","onCreated")
        }

    }


}