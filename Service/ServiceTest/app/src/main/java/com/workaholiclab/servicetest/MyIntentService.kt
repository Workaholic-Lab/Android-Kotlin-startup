package com.workaholiclab.servicetest

import android.app.IntentService
import android.content.Intent
import android.util.Log

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/02/26 17:07
 * @since: Kotlin 1.4
 * @modified by:
 */
class MyIntentService:IntentService("MyIntentService") {
    override fun onHandleIntent(intent: Intent?) {
        //打印当前线程的id
        Log.d("MyIntentService","Thread id is ${Thread.currentThread().name}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyIntentService","onDestroy executed")
    }

}