package com.workaholiclab.genericitytest

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/02/24 23:02
 * @since: Kotlin 1.4
 * @modified by:
 */
class MyClass {
    fun<T:Number> method(param:T):T{
        return param
    }
}

val myClass=MyClass()
val result= myClass.method<Int>(123)