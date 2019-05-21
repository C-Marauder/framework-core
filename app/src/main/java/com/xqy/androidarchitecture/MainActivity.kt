package com.xqy.androidarchitecture

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.androidx.frameworkcore.catchHttpException
import com.xqy.androidarchitecture.fragment.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),MainContracts.MainView {
    override val mFragmentLifecycle: MutableLiveData<Int>? by lazy {
        MutableLiveData<Int>()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        catchHttpException {
            Log.e("===", it.msg)
        }
        addView.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content,HomeFragment.getInstance())
                .commit()
        }

      //  lifecycleScope


    }


}
