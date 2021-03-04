package com.workaholiclab.viewmodeltest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/04 11:00
 * @since: Kotlin 1.4
 * @modified by:
 */
class MainViewModelFactory(private val countReserved:Int):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(countReserved) as T
    }
}