package com.example.activity_test

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_secondary.*

class SecondaryActivity : BaseActivity() {

    //写接口
    companion object{
        fun actionStart(context:Context,data1:String){
            val intent=Intent(context,SecondaryActivity::class.java).apply {
                putExtra("extra_data",data1)
            }
//            intent.putExtra("extra_data",data1)
            context.startActivities(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secondary)
        val extraData=intent.getStringExtra("extra_data")
        Log.d("Secondary_Activity","extraData is $extraData")

        ActivityCollector.addActivity(this)
        button.setOnClickListener {
//            val intent=Intent()
//            intent.putExtra("data_return","Hello First Activity")
//            setResult(RESULT_OK,intent)
//            finish()
        }



    }

    override fun onBackPressed() {
        val intent=Intent()
        intent.putExtra("data_return","Hello First Activity")
        setResult(RESULT_OK,intent)
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }
}