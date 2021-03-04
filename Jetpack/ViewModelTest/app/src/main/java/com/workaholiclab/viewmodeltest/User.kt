package com.workaholiclab.viewmodeltest

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.FileReader

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/04 19:50
 * @since: Kotlin 1.4
 * @modified by:
 */
@Entity
data class User(var firstName:String,var lastName:String,var age :Int){
    @PrimaryKey(autoGenerate = true)
    var id:Long=0
}