package com.workaholiclab.networktest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendRequestBtn.setOnClickListener {
            sendRequestWithHttpURLConnection()
        }
    }

    private fun sendRequestWithHttpURLConnection() {
        //开启线程发起的网络请求

        thread {
            var connection: HttpURLConnection?=null
            try {
                val response = StringBuilder()
                val url = URL("https://www.baidu.com")
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout=8000
                connection.readTimeout=8000
                val input = connection.inputStream
                //下面对获取到的输入流进行读取
                val reader = BufferedReader(InputStreamReader(input))
                reader.use {
                    reader.forEachLine {
                        response.append(it)

                    }
                }
                println(response.toString())
                showResponse(response.toString())
            }catch (e:Exception) {
                e.printStackTrace()
            }finally {
                connection?.disconnect()
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