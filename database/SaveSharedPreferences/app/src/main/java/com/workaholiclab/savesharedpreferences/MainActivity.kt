package com.workaholiclab.savesharedpreferences

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.content.edit as edit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        saveButton.setOnClickListener {
//            val editor=getSharedPreferences("data", Context.MODE_PRIVATE).edit()
//            editor.putString("name","Wendy")
//            editor.putInt("age",20)
//            editor.putBoolean("married",false)
//            editor.apply()
            getSharedPreferences("data",Context.MODE_PRIVATE).edit {
                putString("name","Wendy")
                putInt("age",20)
                putBoolean("married",false)
            }
        }
        restoreButton.setOnClickListener {
            val prefs=getSharedPreferences("data",Context.MODE_PRIVATE)
            val name=prefs.getString("name","")
            val age=prefs.getInt("age",0)
            val married=prefs.getBoolean("married",false)
            println("$name $age $married")
        }
    }

    private fun SharedPreferences.open(block:SharedPreferences.Editor.()->Unit){
        val editor=edit()
        editor.block()//对函数类型参数进行调整
        editor.apply()//提交数据
    }
}