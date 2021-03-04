package com.workaholiclab.viewmodeltest

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/04 21:01
 * @since: Kotlin 1.4
 * @modified by:
 */

//version改成3，两个实体类用逗号分割开来
@Database(version = 3,entities = [User::class,Book::class])
abstract class AppDatabase:RoomDatabase(){
    abstract fun userDao():UserDao
    abstract fun bookDao():BookDao

    companion object{
        //Migration匿名类,传入两个参数表示当前数据库从版本1升级到版本2的时候执行匿名类当中的升级逻辑
        val MIGRATION_1_2 = object  :Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("create table Book (id integer primary key autoincrement not null, name text not null,pages integer not null)")
            }
        }

        val MIGRATION_2_3 = object :Migration(2,3){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table Book add column author text not null default 'unknown'")
            }

        }

        private var instance:AppDatabase?=null

        @Synchronized
        fun getDatabase(context: Context):AppDatabase{
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,AppDatabase::class.java,"app_database")
                //升级数据库逻辑
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build().apply {
                instance=this
            }
        }
    }
}