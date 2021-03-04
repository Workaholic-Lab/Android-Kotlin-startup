package com.workaholiclab.viewmodeltest

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/04 20:21
 * @since: Kotlin 1.4
 * @modified by:
 */
//class MyViewModel:ViewModel() {
//    private val refreshLiveData = MutableLiveData<Any?>()
//    val refreshResult = Transformations.switchMap(refreshLiveData){
//        Repository.refresh()
//    }
//
//    fun refresh(){
//        refreshLiveData.value = refreshLiveData.value
//    }
//}