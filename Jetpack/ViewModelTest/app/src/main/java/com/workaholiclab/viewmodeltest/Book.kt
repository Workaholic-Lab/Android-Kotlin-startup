package com.workaholiclab.viewmodeltest

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/04 21:29
 * @since: Kotlin 1.4
 * @modified by:
 */
@Entity
data class Book(var name:String,var pages:Int,var author:String) {
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
}