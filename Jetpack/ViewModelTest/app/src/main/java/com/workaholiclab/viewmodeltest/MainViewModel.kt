package com.workaholiclab.viewmodeltest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/04 10:49
 * @since: Kotlin 1.4
 * @modified by:
 */
class MainViewModel(countReserved:Int):ViewModel() {
    private val userLiveData = MutableLiveData<User>()
    val userName:LiveData<String> = Transformations.map(userLiveData){user->
        "${user.firstName} ${user.lastName}"
    }

    private val userIdLiveData=MutableLiveData<String>()
    val user:LiveData<User> = Transformations.switchMap(userIdLiveData){
        userId->
        Repository.getUser(userId)
    }

    fun getUser(userId:String){
        userIdLiveData.value=userId
    }

    val counter:LiveData<Int>
    get() = _counter
    private val _counter = MutableLiveData<Int>()
    init {
        _counter.value=countReserved
    }
    fun plusOne(){
        val count = _counter.value?:0
        _counter.value=count+1
    }

    fun clear(){
        _counter.value=0
    }
}