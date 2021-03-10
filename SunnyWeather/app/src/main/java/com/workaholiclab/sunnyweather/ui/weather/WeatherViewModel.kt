package com.workaholiclab.sunnyweather.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.workaholiclab.sunnyweather.logic.Repository
import com.workaholiclab.sunnyweather.logic.model.Location

/**
 * @Description:
 * 天气的ViewModel
 * @author: Gary
 * @date: Created on 2021/03/06 14:25
 * @since: Kotlin 1.4
 * @modified by:
 */
class WeatherViewModel:ViewModel() {

    private val locationLiveData = MutableLiveData<Location>()

    var locationLng=""

    var locationLat=""

    var placeName=""

    val weatherLiveData = Transformations.switchMap(locationLiveData){location->
        Repository.refreshWeather(location.lng,location.lat,placeName)
    }

    fun refreshWeather(lng:String,lat:String){
        locationLiveData.value = Location(lng,lat)
    }
}