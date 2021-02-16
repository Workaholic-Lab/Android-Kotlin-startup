package com.workaholiclab.control

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Lambda表达式实现
        button1.setOnClickListener {
            val input=editText1.text.toString()
            Toast.makeText(this,input,Toast.LENGTH_SHORT).show()
            if(progressBar.visibility==View.VISIBLE){
                progressBar.visibility=View.GONE
            }
            else{
                progressBar.visibility=View.VISIBLE
                progressBar.progress=progressBar.progress+10
            }
        }
        button2.setOnClickListener {
            imageView1.setImageResource(R.drawable.ic_launcher_foreground)
            AlertDialog.Builder(this).apply {
                setTitle("This is a Dialog")
                setMessage("something Important")
                setCancelable(false)
                setPositiveButton("OK"){
                    dialog, which ->
                }
                setNegativeButton("Cancel"){
                    dialog, which ->
                }
                show()
            }
        }
    }
}