package com.workaholiclab.okhttptest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader
import java.lang.Exception
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val keyMain="MainActivity"
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
//            HttpUtil.sendOkHttpRequest("https://www.baidu.com", object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    //对异常进行处理
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    //得到服务器返回的具体内容
//                    val responseData = response.body?.string()
//                }
//            })
    }

    }

    private fun sendRequestWithOkHttp() {
        thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    //这里吧HTTP请求·的地址改成了下面这个xml
                    //指定访问服务器地址是计算机本机
                    .url("http://10.0.2.2/get_data.xml")
                    //10.0.2.2对于模拟器来说就是计算机本机的IP地址
                    .build()
                val response = client.newCall(request).execute()
                val responseData= response.body?.string()
                if(responseData!=null){
//                    showResponse(responseData)
                    //解析XML，不再用展示
                    parseXMLWithPull(responseData)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun parseXMLWithPull(xmlData: String) {
        try{
            //首先创建XmlPullParserFactory实例
            val factory = XmlPullParserFactory.newInstance()
            //接住实例得到XmlPullParser对象
            val xmlPullParser = factory.newPullParser()
            //调用setInput方法将服务器返回的XML数据设置进去
            xmlPullParser.setInput(StringReader(xmlData))
            //解析的过程当中可以通过getEventType获取当前解析的事件
            var eventType = xmlPullParser.eventType
            var id =""
            var name =""
            var version = ""
            //然后在while循环当中不断解析，如果解析不等于XmlPullParser.END_DOCUMENT说明解析工作还没有完成那个，调用next方法
            while (eventType!=XmlPullParser.END_DOCUMENT){
                //获取当前节点的名字
                val nodeName = xmlPullParser.name
                when(eventType){
                    //开始解析某个节点
                    XmlPullParser.START_TAG->{
                        when(nodeName){
                            //发现对应的就调用nextText方法来获取节点内的具体内容
                            "id" -> id =xmlPullParser.nextText()
                            "name"-> name=xmlPullParser.nextText()
                            "version" -> version=xmlPullParser.nextText()
                        }
                    }
                    //完成某个节点的解析
                    XmlPullParser.END_TAG ->{
                        //每当解析完一个app将其打印出来
                        if("app"==nodeName){
                            Log.d(keyMain,"id is $id")
                            Log.d(keyMain,"name is $name")
                            Log.d(keyMain,"version is $version")
                        }
                    }
                }
                eventType=xmlPullParser.next()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun showResponse(response: String) {
        runOnUiThread{
            //在这里进行UI操作
            responseText.text = response
        }
    }


}