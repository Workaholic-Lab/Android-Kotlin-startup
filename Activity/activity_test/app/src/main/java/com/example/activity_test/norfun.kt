package com.example.activity_test

import kotlin.text.StringBuilder

/**
 * @Description: To test normalization function in kotlin: with, run, apply
 * @author: Gary
 * @date: Created on 2021/02/16 15:31
 * @since: Kotlin 1.4
 * @modified by:
 */
//Lambda表达式测试
val list= mutableListOf<String>("Apple","Banana","Orange")
val map= mapOf("Apple" to 1, "Banana" to 2)

fun main()
{
    for ((fruit,number) in map){
        println("fruit is $fruit, and number is $number")
    }
    list.add("Watermelon")
    val nList=list.filter { it.length<=5 }.map { it.toUpperCase() } //it.length<=5就是函数体
    for(fruit in nList){
        println(fruit)
    }


//    val builder=StringBuilder()
//    builder.append("Start eating fruits.\n")
//    for (fruit in list){
//        builder.append(fruit).append("\n")
//    }
//    builder.append("Finish Eating.")
//    val result=builder.toString()
//    println(builder)

// with:
//    val result=with(java.lang.StringBuilder()){
//        append("Start Eating.\n")
//        for (fruit in list){
//            append(fruit).append("\n")
//        }
//        append("Finish eating")
//        toString()
//    }
//    println(result)

    //run
//    val result1=java.lang.StringBuilder().run{
//        append("Start Eating.\n")
//        for (fruit in list){
//            append(fruit).append("\n")
//        }
//        append("Finish eating")
//        toString()
//    }
//    println(result1)

    //
    val result2=java.lang.StringBuilder().apply{
        append("Start Eating.\n")
        for (fruit in list){
            append(fruit).append("\n")
        }
        append("Finish eating")
        toString()
    }
    println(result2)
}

//let
fun doSomething(fruit: String?){
    fruit?.let {
        it.toUpperCase()
        it.hashCode()
    }
}

