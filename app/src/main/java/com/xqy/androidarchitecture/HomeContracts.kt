package com.xqy.androidarchitecture
import android.util.Log
import com.androidx.frameworkcore.mvi.*
import com.androidx.processor.Contract
import com.androidx.processor.Presenter

@Contract(["HomeFragment"])
class HomeContracts {

    interface HomeView: AppView {
    }

    @Presenter
    class HomePresenter(mView: HomeView) : AppPresenter<HomeView>(mView) {
        override fun onCreated() {
            super.onCreated()
            Log.e("->>>--","onCreated")

        }

    }


}