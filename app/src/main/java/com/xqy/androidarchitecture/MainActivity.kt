package com.xqy.androidarchitecture

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.MutableLiveData
import com.xqy.androidarchitecture.fragment.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        catchHttpException {
//            Log.e("===", it.msg)
//        }


        addView.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content,HomeFragment.getInstance())
                .commit()
        }

      //  lifecycleScope


    }

    override fun onStart() {
        super.onStart()
        Log.e("===","===onStart==")
    }

    override fun onResume() {
        super.onResume()
        Log.e("===","===onResume==")

    }
}


