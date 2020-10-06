package com.example.activity_test

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.first_layout.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first_layout)
        doSomething()
        button1.setOnClickListener {
            val data="Hello Secondary Activity"
//            val intent=Intent(this,SecondaryActivity::class.java)
//            intent.putExtra("extra_data",data)
//            startActivity(intent)
            SecondaryActivity.actionStart(this,data)
            //显示调用Intent
//            val intent1=Intent(this,SecondaryActivity::class.java)
//            startActivityForResult(intent1,1)
        }

        ActivityCollector.addActivity(this)
        button2.setOnClickListener {
            val intent=Intent(Intent.ACTION_VIEW)
            intent.data=Uri.parse("https://www.baidu.com/")
            startActivity(intent)
        }
        button3.setOnClickListener {
            val intent=Intent(Intent.ACTION_DIAL)
            intent.data= Uri.parse("tel:10086")
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1->if(resultCode== RESULT_OK){
                val returnedData=data?.getStringExtra("data_return")
                Log.d("FirstActivity","returned data is $returnedData")
            }
        }
    }

    //创建右上角菜单
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add_item->Toast.makeText(this,"ADD",Toast.LENGTH_SHORT).show()
            R.id.remove_item->Toast.makeText(this,"REMOVE",Toast.LENGTH_SHORT).show()
        }
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }
}