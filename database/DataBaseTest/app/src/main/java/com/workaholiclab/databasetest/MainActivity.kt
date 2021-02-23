package com.workaholiclab.databasetest

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dbHelper=MyDatabaseHelper(this,"BookStore.db",3)
        createDatabase.setOnClickListener {
            dbHelper.writableDatabase
        }
        //添加数据
        addData.setOnClickListener {
            val db=dbHelper.writableDatabase
            val values1=ContentValues().apply {
                //开始组装第一条数据
                put("name","The Da Vinci Code")
                put("author","Dan Brown")
                put("pages",454)
                put("price",19.95)
            }
            db.insert("Book",null,values1)//插入第一条数据
            val values2=ContentValues().apply {
                //开始组装第一条数据
                put("name","First Code")
                put("author","guolin")
                put("pages",692)
                put("price",99.00)
            }
            db.insert("Book",null,values2)//插入第二条数据

            Toast.makeText(this,"成功添加数据",Toast.LENGTH_SHORT).show()
        }

        //更新数据
        updateData.setOnClickListener {
            val db=dbHelper.writableDatabase
            val values=ContentValues()
            values.put("price",10.99)
            db.update("Book",values,"name = ?", arrayOf("The Da Vinvi Code"))
            Toast.makeText(this,"更新数据成功",Toast.LENGTH_SHORT).show()
        }

        //删除数据
        deleteData.setOnClickListener {
            val db=dbHelper.writableDatabase
            db.delete("Book","pages > ?", arrayOf("500"))
            Toast.makeText(this,"删除数据成功",Toast.LENGTH_SHORT).show()
        }

        //查询数据
        queryData.setOnClickListener {
            val db=dbHelper.writableDatabase
            //查询Book表中所有的标准数据
            val cursor=db.query("Book",null,null,null,null,null,null)
            if(cursor.moveToFirst()){
                do {
                    //遍历Cursor对象,取出数据并打印
                    val name=cursor.getString(cursor.getColumnIndex("name"))
                    val author=cursor.getString(cursor.getColumnIndex("author"))
                    val pages=cursor.getInt(cursor.getColumnIndex("pages"))
                    val price=cursor.getDouble(cursor.getColumnIndex("price"))
                    println("$name $author $pages $price")
                }while (cursor.moveToNext())
            }
            cursor.close()
            Toast.makeText(this,"查询数据成功",Toast.LENGTH_SHORT).show()
        }

        //使用事务
        replaceData.setOnClickListener {
            val db=dbHelper.writableDatabase
            db.beginTransaction()//开启事务
            try {
                db.delete("Book",null,null)
//                if(true){
//                    //手动抛出一个异常让事务失败
//                    throw NullPointerException()
//                }
                val values=ContentValues().apply {
                    put("name","Game of Thrones")
                    put("author","George Martin")
                    put("pages",720)
                    put("price",20.85)
                }
                db.insert("Book",null,values)
                db.setTransactionSuccessful()//事务已经执行成功
            }catch (e:IOException){
                e.printStackTrace()
            }finally {
                db.endTransaction()//结束事务
            }
        }
    }
}