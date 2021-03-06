package com.workaholiclab.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * @Description:
 * 每天数据模型
 * @author: Gary
 * @date: Created on 2021/03/06 12:27
 * @since: Kotlin 1.4
 * @modified by:
 */
data class DailyResponse(val status:String,val result:Result) {
    data class Result(val daily:Daily)

    data class Daily(val temperature :List<Temperature>,val skycon:List<Skycon>,@SerializedName("life_index") val lifeIndex:LifeIndex)

    data class Temperature(val max:Float,val min:Float)

    data class Skycon(val value: String,val date:Date)

    data class LifeIndex(val coldRisk:List<LifeDescription>,val ultraviolet:List<LifeDescription>,val carWashing:List<LifeDescription>,val dressing:List<LifeDescription>)

    data class LifeDescription(val desc:String)
}