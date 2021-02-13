package com.example.ui_demo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val fruitList=ArrayList<Fruit>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFruits();//初始化数据
        val adapter=FruitAdapter(this,R.layout.fruit_item,fruitList)
        listView.adapter=adapter
        listView.setOnItemClickListener { parent, view, position, id ->
            val fruit = fruitList[position]
            Toast.makeText(this,fruit.name.toString()+" is Alan's favourite",Toast.LENGTH_SHORT).show()
        }
    }

    private fun initFruits() {
        repeat(2){
            fruitList.add(Fruit("Apple",R.drawable.apple_pic))
            fruitList.add(Fruit("banana",R.drawable.banana_pic))
            fruitList.add(Fruit("orange",R.drawable.orange_pic))
        }
    }
}