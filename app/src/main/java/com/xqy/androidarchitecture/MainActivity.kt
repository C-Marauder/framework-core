package com.xqy.androidarchitecture

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xqy.androidarchitecture.fragment.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),HomeContracts.HomeView {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addView.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content,HomeFragment.getInstance())
                .commit()
        }

    }


}
