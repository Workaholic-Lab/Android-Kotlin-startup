package com.example.activity_test

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2020/10/06 23:04
 * @since: Kotlin 1.4
 * @modified by:
 */
open class BaseActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BaseActivity",javaClass.simpleName)
    }
}