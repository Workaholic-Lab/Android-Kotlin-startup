package com.workaholiclab.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

/**
 * @Description:
 * 根据Json数据构建实时天气模型
 * @author: Gary
 * @date: Created on 2021/03/06 12:24
 * @since: Kotlin 1.4
 * @modified by:
 */
data class RealtimeResponse(val status:String,val result: Result){
    data class Result(val realtime:Realtime)
    data class Realtime(val skycon:String,val temperature:Float,@SerializedName("air_quality")val airQuality:AirQuality)
    data class AirQuality(val aqi:AQI)
    data class AQI(val chn:Float)
}