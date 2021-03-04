package com.workaholiclab.viewmodeltest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/04 19:58
 * @since: Kotlin 1.4
 * @modified by:
 */
object Repository {
    fun getUser(userId:String):LiveData<User>{
        val liveData = MutableLiveData<User>()
        liveData.value=User(userId,userId,0)
        return liveData
    }
}