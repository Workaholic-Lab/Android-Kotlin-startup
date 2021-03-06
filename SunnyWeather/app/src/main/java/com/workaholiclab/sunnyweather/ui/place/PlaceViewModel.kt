package com.workaholiclab.sunnyweather.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.workaholiclab.sunnyweather.logic.Repository
import com.workaholiclab.sunnyweather.logic.dao.PlaceDao
import com.workaholiclab.sunnyweather.logic.model.Place

/**
 * @Description:
 * ViewModel
 * ViewModel层，相当于逻辑层与UI层之间的一个桥梁
 * @author: Gary
 * @date: Created on 2021/03/06 10:53
 * @since: Kotlin 1.4
 * @modified by:
 */

class PlaceViewModel:ViewModel(){

    private val searchLiveData = MutableLiveData<String>()

    //界面上显示的城市数据进行缓存
    val placeList = ArrayList<Place>()

    //转化成为另外一个可观察的LiveData对象
    val placeLiveData = Transformations.switchMap(searchLiveData){query->
        Repository.searchPlaces(query)
    }

    fun searchPlaces(query:String){
        searchLiveData.value = query
    }

    fun savePlace(place:Place) = PlaceDao.savePlace(place)

    fun getSavedPlace()= PlaceDao.getSavedPlace()

    fun isPlaceSaved()= PlaceDao.isPlaceSaved()
}