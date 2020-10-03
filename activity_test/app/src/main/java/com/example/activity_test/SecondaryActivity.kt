package com.example.activity_test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_secondary.*

class SecondaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secondary)
//        val extraData=intent.getStringExtra("extra_data")
//        Log.d("Secondary_Activity","extraData is $extraData")

        button.setOnClickListener {
            val intent=Intent()
            intent.putExtra("data_return","Hello First Activity")
            setResult(RESULT_OK,intent)
            finish()
        }



    }

    override fun onBackPressed() {
        val intent=Intent()
        intent.putExtra("data_return","Hello First Activity")
        setResult(RESULT_OK,intent)
        finish()
    }
}