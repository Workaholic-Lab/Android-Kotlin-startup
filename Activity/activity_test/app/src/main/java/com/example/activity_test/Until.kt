package com.example.activity_test

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/02/16 16:15
 * @since: Kotlin 1.4
 * @modified by:
 */

class Until{
    fun doAction1(){
        println("Action1")
    }

    companion object{
        @JvmStatic
        fun doAction2()
        {
            println("Action2")
        }
    }
}