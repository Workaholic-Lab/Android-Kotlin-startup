package com.workaholiclab.sunnyweather.logic.network

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * @Description:
 * 创建Retrofit后建起
 * @author: Gary
 * @date: Created on 2021/03/06 10:10
 * @since: Kotlin 1.4
 * @modified by:
 */
object ServiceCreator {
    private const val BASE_URL ="https://api.caiyunapp.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun<T>create(serviceClass: Class<T>):T = retrofit.create(serviceClass)

    inline fun<reified T>create():T= create(T::class.java)
}