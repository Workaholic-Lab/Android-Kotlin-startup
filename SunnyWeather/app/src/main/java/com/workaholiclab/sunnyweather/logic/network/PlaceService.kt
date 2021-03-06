package com.workaholiclab.sunnyweather.logic.network

import com.workaholiclab.sunnyweather.SunnyWeatherApplication
import com.workaholiclab.sunnyweather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @Description:
 * 访问API的Retrofit接口，返回数据解析为我们的对象
 * @author: Gary
 * @date: Created on 2021/03/06 9:47
 * @since: Kotlin 1.4
 * @modified by:
 */
interface PlaceService {
    @GET("v2/place?token=${SunnyWeatherApplication.TOKEN}&lang=zh_CN")
    fun searchPlaces(@Query("query") query :String):Call<PlaceResponse>//返回JSON数据会自动解析成为PlaceResponse对象
}