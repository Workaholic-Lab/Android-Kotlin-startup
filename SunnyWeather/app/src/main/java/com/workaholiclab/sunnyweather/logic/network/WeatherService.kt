package com.workaholiclab.sunnyweather.logic.network

import com.workaholiclab.sunnyweather.SunnyWeatherApplication
import com.workaholiclab.sunnyweather.logic.model.DailyResponse
import com.workaholiclab.sunnyweather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/06 12:39
 * @since: Kotlin 1.4
 * @modified by:
 */
interface WeatherService {
    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng")lng:String,@Path("lat")lat:String):Call<RealtimeResponse>

    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng")lng:String,@Path("lat")lat:String):Call<DailyResponse>
}