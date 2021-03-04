package com.workaholiclab.viewmodeltest

import androidx.lifecycle.ViewModel

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/04 10:49
 * @since: Kotlin 1.4
 * @modified by:
 */
class MainViewModel(countReserved:Int):ViewModel() {
    var counter = countReserved
}