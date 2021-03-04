package com.workaholiclab.lifecycletest

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/04 11:45
 * @since: Kotlin 1.4
 * @modified by:
 */
class MyObserver(val lifecycle: Lifecycle) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun activityStart(){
        Log.d("MyObserver","activityStart")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun activityStop(){
        Log.d("MyObserver","activityStop")
    }
}