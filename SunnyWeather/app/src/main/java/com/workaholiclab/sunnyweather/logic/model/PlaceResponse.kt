package com.workaholiclab.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

/**
 * @Description:
 * 数据模型 按照搜索城市接口返回的JSON格式来定义的
 * @author: Gary
 * @date: Created on 2021/03/06 9:36
 * @since: Kotlin 1.4
 * @modified by:
 */
data class PlaceResponse(val status:String,val places: List<Place>)


data class Place(val name:String,val location:Location, @SerializedName("formatted_address") val address:String)//两者命名有些不同的时候，SerializedName注解让JSON字段和Kotlin字段建立映射关系

data class Location(val lng:String,val lat:String)