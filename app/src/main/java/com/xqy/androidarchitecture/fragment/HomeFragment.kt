package com.xqy.androidarchitecture.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.xqy.androidarchitecture.HomeContracts
import com.xqy.androidarchitecture.R

class HomeFragment:Fragment(),HomeContracts.HomeView {
    override val mFragmentLifecycle: MutableLiveData<Int>? by lazy {
        MutableLiveData<Int>()
    }

    companion object{
        fun  getInstance() = HomeFragment()
    }
    init {
        with(lifecycleScope){
            launchWhenCreated {
                Log.e("==","==launchWhenCreated==")
            }

            launchWhenStarted {
                Log.e("==","==launchWhenStarted==")
            }
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e("====","onCreateView=====>>")

        return inflater.inflate(R.layout.f_home,container,false)
    }

    override fun onStart() {
        super.onStart()
        Log.e("====","onStart=====>>")

    }


}