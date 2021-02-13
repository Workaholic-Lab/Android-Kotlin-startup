package com.example.fragment_demo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val f1=TestFragment()
        supportFragmentManager.beginTransaction().add(R.id.layout_container,f1).commit()

        btn_remove.setOnClickListener {
            supportFragmentManager.beginTransaction().remove(f1).commit()
        }

        btn_replace.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.layout_container,SecondFragment()).addToBackStack(null).commit()
        }
    }
}