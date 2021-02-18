package com.workaholiclab.listviewtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val fruitList=ArrayList<Fruit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFruits()
        val adapter=FruitAdapter(this,R.layout.fruit_item,fruitList)
        lv1.adapter=adapter
        lv1.setOnItemClickListener { _, _, position, _ ->
            val fruit=fruitList[position]
            Toast.makeText(this,fruit.name,Toast.LENGTH_SHORT).show()
        }
//        val adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data)
//        lv1.adapter=adapter
    }
    private fun initFruits() {
        val data= listOf<String>("Apple","Banana","Pear","Watermelon","Grape","Pineapple","Strawberry","Cherry","Mango","Orange")
        repeat(2){
            for (fn in data)
            {
                fruitList.add(Fruit(fn,R.drawable.ic_launcher_background))
            }
        }
    }
}