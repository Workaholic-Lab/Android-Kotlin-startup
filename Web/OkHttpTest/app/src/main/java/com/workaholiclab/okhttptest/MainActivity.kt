package com.workaholiclab.okhttptest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fun testOkHttp() {
            //创建实例
            val client = OkHttpClient()
            //发起一条Http请求，创建一个Request对象
            //val request = Request.Builder().build()//这样子只能创建一个空的request，应该像下面一样：
            //赋值
            val request = Request.Builder().url("https://www.baidu.com").build()
            //newCall()来创建一个Call对象
            val response=client.newCall(request).execute()
            //Request对象就是服务器返回的数据了，我们采用如下写法来得到返回的数据
            val responseData=response.body?.string()

            //如果是POST的话会麻烦一点点，如下：
            //先构建Request Body对象来存放待提交的数据
            val requestBody = FormBody.Builder().add("username","admin").add("password","123456").build()
            //调用post方法将RequestBody对象传入
            val requestPost=Request.Builder().url("https://www.baidu.com").post(requestBody).build()
            //后面就和Get一样调用execute()方法来发送并请求获取服务器返回的数据即可
        }

        sendRequestBtn.setOnClickListener {
            sendRequestWithOkHttp()
        }

    }

    private fun sendRequestWithOkHttp() {
        thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url("https://www.baidu.com").build()
                val response = client.newCall(request).execute()
                val responseData= response.body?.string()
                if(responseData!=null){
                    showResponse(responseData)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun showResponse(response: String) {
        runOnUiThread{
            //在这里进行UI操作
            responseText.text = response
        }
    }


}