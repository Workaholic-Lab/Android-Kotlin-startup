package com.workaholiclab.bestparcticefragment

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/02/20 12:16
 * @since: Kotlin 1.4
 * @modified by:
 */
class Obj {
    operator fun plus(obj:Obj):Obj{
        //处理相加的逻辑代码
    }
}


val obj1=Obj()
val obj2=Obj()
val obj3= obj1+ obj2


operator fun String.times(n:Int)=repeat(n)
//实现上面的重载后即可
fun getRandomLengthString(str:String)=str*(1..20).random()