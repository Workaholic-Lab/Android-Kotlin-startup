package com.workaholiclab.databasetest

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/02/23 17:08
 * @since: Kotlin 1.4
 * @modified by:
 */
class MyDatabaseHelper(val context: Context,name:String,version:Int):SQLiteOpenHelper(context,name,null,version) {
    private val createBook="create table Book ("+
            "id integer primary key autoincrement,"+
            "author text,"+
            "price real,"+
            "pages integer,"+
            "name text,"+
            "category_id integer)"
    private val createCategory="create table Category ("+
            "id integer primary key autoincrement,"+
            "category_name text,"+
            "category_code integer)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createBook)
        db?.execSQL(createCategory)
        Toast.makeText(context,"Create succeeded",Toast.LENGTH_SHORT).show()
    }

    //升级数据库,比如像加多一张表
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        if(oldVersion<=1)
//            db?.execSQL(createCategory)
        if (oldVersion<=2){
            db?.execSQL("alter table Book add column category_id integer")
        }
    }

}