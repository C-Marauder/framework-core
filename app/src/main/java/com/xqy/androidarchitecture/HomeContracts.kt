package com.xqy.androidarchitecture
import android.util.Log
import com.androidx.processor.Contract
import com.androidx.processor.Presenter
import com.androidx.frameworkcore.mvi.AppPresenter
import com.androidx.frameworkcore.mvi.AppView
import com.androidx.frameworkcore.request

@Contract(["MainActivity","HomeFragment"])
class HomeContracts {

    interface HomeView: AppView {
    }

    @Presenter
    class HomePresenter(mView: HomeView) : AppPresenter<HomeView>(mView) {

        override fun onCreate() {
            super.onCreate()
            Log.e("===","=====>>>")
            mView.request<String, String>("getThings", "2", {

            }, {

            }, {

            })


        }
    }


}