package com.workaholiclab.okhttptest

import java.lang.Exception

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/02 20:27
 * @since: Kotlin 1.4
 * @modified by:
 */
interface HttpCallbackListener {
    fun onFinish(response:String)
    fun onError(e:Exception)
}