package com.workaholiclab.viewmodeltest

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/04 21:30
 * @since: Kotlin 1.4
 * @modified by:
 */
@Dao
interface BookDao {
    @Insert
    fun insertBook(book:Book):Long

    @Query("select * from Book")
    fun loadAllBooks():List<Book>
}