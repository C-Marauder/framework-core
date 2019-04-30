package com.xqy.androidarchitecture.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xqy.androidarchitecture.HomeContracts
import com.xqy.androidarchitecture.R

class HomeFragment:Fragment(),HomeContracts.HomeView {
    companion object{
        fun  getInstance() = HomeFragment()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_home,container,false)
    }
}