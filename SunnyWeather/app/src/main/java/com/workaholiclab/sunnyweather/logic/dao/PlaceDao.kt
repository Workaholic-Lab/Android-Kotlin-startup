package com.workaholiclab.sunnyweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.workaholiclab.sunnyweather.SunnyWeatherApplication
import com.workaholiclab.sunnyweather.logic.model.Place

/**
 * @Description:
 * 记录选中的城市
 * 由于不属于关系模型，所以用简单的SharedPreferences存储就算了
 * @author: Gary
 * @date: Created on 2021/03/06 20:47
 * @since: Kotlin 1.4
 * @modified by:
 */
object PlaceDao {
    //保存
    fun savePlace(place: Place){
        sharePreferences().edit(){
            putString("place", Gson().toJson(place))
        }
    }

    //Json字符从SharedPreferences文件中读取出来
    fun getSavedPlace():Place{
        val placeJson = sharePreferences().getString("place","")
        return Gson().fromJson(placeJson,Place::class.java)
    }

    //用于判断是否有数据被存储
    fun isPlaceSaved() = sharePreferences().contains("place")

    private fun sharePreferences() = SunnyWeatherApplication.context.getSharedPreferences("sunny_weather",Context.MODE_PRIVATE)
}