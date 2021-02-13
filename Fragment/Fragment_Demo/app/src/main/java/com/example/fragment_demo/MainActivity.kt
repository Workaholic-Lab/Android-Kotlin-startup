package com.example.fragment_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /**
         * 使用代码创建fragment
         */
//        button.setOnClickListener {
//            replaceFragment(DownFragment())
//        }
        val fragmentManager:FragmentManager=supportFragmentManager
        val transaction=fragmentManager.beginTransaction()
        transaction.add(R.id.up_fragment,UpFragment())
        transaction.add(R.id.down_fragment,DownFragment())
        transaction.commit()
    }
//    private fun replaceFragment(fragment:Fragment)
//    {
//        val fragmentManager:FragmentManager=supportFragmentManager
//        val transaction=fragmentManager.beginTransaction()
//        transaction.replace(R.id.up_fragment,fragment)
//        transaction.commit()
//    }
}