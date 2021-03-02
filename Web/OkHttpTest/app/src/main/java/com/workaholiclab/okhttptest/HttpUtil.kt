package com.workaholiclab.okhttptest

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.URL
import kotlin.concurrent.thread

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/02 20:17
 * @since: Kotlin 1.4
 * @modified by:
 */
object HttpUtil {
    fun sendHttpRequest(address: String,listener: HttpCallbackListener){
        thread {
            var connection: HttpURLConnection?=null
            try {
                val response = StringBuilder()
                val url = URL(address)
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
                //回调onFinishI()方法
                listener.onFinish(response.toString())
            }catch (e: Exception) {
                e.printStackTrace()
                //回调onError()方法
                listener.onError(e)
            }finally {
                connection?.disconnect()
            }
        }
    }
    fun sendOkHttpRequest(address: String,callback:okhttp3.Callback){
        val client = OkHttpClient()
        val request = Request.Builder().url(address).build()
        client.newCall(request).enqueue(callback)
    }
}

