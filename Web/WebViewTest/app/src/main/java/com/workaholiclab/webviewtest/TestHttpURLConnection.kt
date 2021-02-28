package com.workaholiclab.webviewtest

import java.net.HttpURLConnection
import java.net.URL

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/02/28 20:21
 * @since: Kotlin 1.4
 * @modified by:
 */




class TestHttpURLConnection {
    private val url = URL("https://www.baidu.com/")
    private val connection = url.openConnection() as HttpURLConnection
    fun me(){
        connection.requestMethod = "GET"
        connection.connectTimeout = 8000
        connection.readTimeout = 8000
        val input = connection.inputStream
        connection.disconnect()
    }

}