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

    companion object{
        fun  getInstance() = HomeFragment()
    }
    private val observer = LifecycleEventObserver { source, event ->
        if (source.lifecycle.currentState > Lifecycle.State.CREATED) {
           if (event == Lifecycle.Event.ON_START){
               Log.e("===","event")

           }else{
               Log.e("===","===>>>")
           }

        }
    }
    init {


        //lifecycle.addObserver(observer)
        lifecycleScope.launchWhenCreated {

        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("====","onCreate=====>>")


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e("====","onCreateView=====>>")

        return inflater.inflate(R.layout.f_home,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("====","onViewCreated=====>>")

    }

    override fun onStart() {
        super.onStart()
        Log.e("====","onStart=====>>")

    }


}

