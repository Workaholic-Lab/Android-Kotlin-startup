package com.workaholiclab.improvedfun

import java.lang.StringBuilder

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/02/22 12:02
 * @since: Kotlin 1.4
 * @modified by:
 */
inline fun num1AndNum2(num1:Int,num2:Int,operation:(Int,Int)->Int):Int{
    return operation(num1,num2)
}


fun plus(num1:Int,num2:Int):Int{
    return num1+num2
}

fun minus(num1:Int,num2: Int):Int{
    return num1-num2
}

fun StringBuilder.build(block:StringBuilder.()->Unit):StringBuilder{
    block()
    return this
}

fun main(){
    val num1=80
    val num2=50
    val r1= num1AndNum2(num1,num2,::plus)
    val r2= num1AndNum2(num1,num2,::minus)
    println("r1 is $r1")
    println("r2 is $r2")
    val list= listOf("Apple","Banana","Orange","Pear","Grape")
    val result=StringBuilder().build {
        append("Start eating fruits")
        for(fruit in list)
        {
            append(fruit).append("\n")
        }
        append("Ate all fruits")
    }
    println(result.toString())
}