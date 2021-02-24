package com.workaholiclab.providertest

import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.contentValuesOf
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var bookId:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addData.setOnClickListener {
            //添加数据
            val uri= Uri.parse("content://com.workaholiclab.databasetest.provider/book")
            val values=contentValuesOf("name" to "A Clash of Kings","author" to " George Martin","pages" to 1040,"price" to 22.85)
            val newUri=contentResolver.insert(uri,values)//insert方法会返回一个Uri对象，这个对象包含了新增数据的id
            bookId=newUri?.pathSegments?.get(1)//我们通过getPathSegments方法将这个id取出，稍后会用到
        }
        queryData.setOnClickListener {
            //查询数据
            val uri = Uri.parse("content://com.workaholiclab.databasetest.provider/book")
            contentResolver.query(uri,null,null,null,null)?.apply {
                while(moveToNext()){
                    val name=getString(getColumnIndex("name"))
                    val author=getString(getColumnIndex("author"))
                    val pages=getString(getColumnIndex("pages"))
                    val price=getString(getColumnIndex("price"))
                    println("$name $author $pages $price")
                }
                close()
            }
        }

        updateData.setOnClickListener {
            //更新数据
            bookId?.let {
                val uri=Uri.parse("content://com.workaholiclab.databasetest.provider/book/$it")
                val values= contentValuesOf("name" to "A storm of Swords","pages" to 1216,"price" to 24.05)
                contentResolver.update(uri,values,null,null)
            }
        }

        deleteData.setOnClickListener {
            //删除数据
            bookId?.let {
                val uri= Uri.parse("content://com.workaholiclab.databasetest.provider/book/$it")
                contentResolver.delete(uri,null,null)
            }
        }
    }
}