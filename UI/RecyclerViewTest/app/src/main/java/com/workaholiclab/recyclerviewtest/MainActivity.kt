package com.workaholiclab.recyclerviewtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private val fruitList=ArrayList<Fruit>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFruits()
        val layoutManager=StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
        rv1.layoutManager=layoutManager
        val adapter=FruitAdapter(fruitList)
        rv1.adapter=adapter
    }
    private fun initFruits() {
        val data= listOf<String>("Apple","Banana","Pear","Watermelon","Grape","Pineapple","Strawberry","Cherry","Mango","Orange")
        repeat(2){
            for (fn in data)
            {
                fruitList.add(Fruit(getRandomLengthString(fn),R.drawable.ic_launcher_background))
            }
        }
    }

    private fun getRandomLengthString(str: String): String {
        val n=(1..20).random()
        val builder=StringBuilder()
        repeat(n){
            builder.append(str)
        }
        return builder.toString()
    }
}