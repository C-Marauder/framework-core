package com.xqy.androidarchitecture

import android.util.Log
import com.androidx.frameworkcore.execute
import com.androidx.processor.Contract
import com.androidx.processor.Presenter
import com.androidx.frameworkcore.mvi.AppPresenter
import com.androidx.frameworkcore.mvi.AppView


@Contract(["MainActivity"])
class MainContracts {

    interface MainView : AppView {
    }

    @Presenter
    class MianPresenter(mView: MainView) : AppPresenter<MainView>(mView) {
        override fun onCreated() {
            super.onCreated()
            mLifecycleScope.launchWhenCreated {
                mView.execute<String, String>("getThings", null, {
                    Log.e("loading", ">>>>")

                }, {
                    Log.e("success", ">>>>")
                }, {
                    Log.e("error", it)

                })
            }
        }

    }


}