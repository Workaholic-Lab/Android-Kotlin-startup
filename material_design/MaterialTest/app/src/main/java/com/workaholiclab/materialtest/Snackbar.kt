package com.workaholiclab.materialtest

import android.security.identity.AccessControlProfileId
import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/04 9:56
 * @since: Kotlin 1.4
 * @modified by:
 */

//fun View.showSnackbar(text:String,duration:Int=Snackbar.LENGTH_SHORT){
//    Snackbar.make(this,text,duration).show()
//}
//fun View.showSnackbar(resId: Int,duration:Int=Snackbar.LENGTH_SHORT){
//    Snackbar.make(this,resId,duration).show()

    //支持setAction写法
    fun View.showSnackbar(text:String, actionText:String?=null, duration: Int=Snackbar.LENGTH_SHORT, block:(()->Unit)?=null){
        val snackbar = Snackbar.make(this,text,duration)
    if(actionText!=null&&block!=null){
        snackbar.setAction(actionText){
            block()
        }
    }
    snackbar.show()
}
fun View.showSnackbar(resId:Int, actionText:String?=null, duration: Int=Snackbar.LENGTH_SHORT, block:(()->Unit)?=null){
        val snackbar = Snackbar.make(this,resId,duration)
    if(actionText!=null&&block!=null){
        snackbar.setAction(actionText){
            block()
        }
    }
    snackbar.show()
}