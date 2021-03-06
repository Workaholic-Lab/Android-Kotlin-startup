package com.workaholiclab.sunnyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * @Description:
 *  用于获取全局Context
 * @author: Gary
 * @date: Created on 2021/03/06 9:31
 * @since: Kotlin 1.4
 * @modified by:
 */
class SunnyWeatherApplication:Application() {

    companion object{
        const val TOKEN ="TAkhjf8d1nlSlspN"//输入彩云天气申请到的指令牌
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}