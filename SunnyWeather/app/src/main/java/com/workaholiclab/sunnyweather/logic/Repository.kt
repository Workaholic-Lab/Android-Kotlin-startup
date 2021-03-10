package com.workaholiclab.sunnyweather.logic


import android.content.Context
import androidx.lifecycle.liveData
import com.workaholiclab.sunnyweather.logic.dao.PlaceDao
import com.workaholiclab.sunnyweather.logic.model.Place
import com.workaholiclab.sunnyweather.logic.model.Weather
import com.workaholiclab.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext


/**
 * @Description:
 * 仓库层的统一封装入口
 * @author: Gary
 * @date: Created on 2021/03/06 10:35
 * @since: Kotlin 1.4
 * @modified by:
 */
object Repository {
    //搜索地址
    //DisPatchers.IO表示代码块中所有的代码都运行在子线程中，Android不允许在主线程进行网络请求，诸如读写数据库之类的本地数据操作也不建议写在主线程，因此在仓库层这里进行一次线程转换
    fun searchPlaces(query:String) = fire(Dispatchers.IO) {//可以调用任意挂起函数了（有一个挂起函数的上下文）
//        val result = try {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if(placeResponse.status=="ok"){
                val places = placeResponse.places
                Result.success(places)
            }else{
             Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
//        }catch (e:Exception){
//            Result.failure<List<Place>>(e)
//        }
//        //类似于LiveData的setValue方法，只不过这里我们无法直接取得返回LiveData的对象
//        emit(result)
    }


    //更新天气情况
    fun refreshWeather(lng:String,lat:String,placeName: String)= fire(Dispatchers.IO) {

            coroutineScope {
                //async中发起网络请求，协程中才能调用，所以要在coroutineScope进行
                val deferredRealtime =async {
                    SunnyWeatherNetwork.getRealtimeWeather(lng,lat)
                }
                val deferredDaily = async {
                    SunnyWeatherNetwork.getDailyWeather(lng,lat)
                }

                //在调用await()方法可以保证只有在两个网络请求成功时候才会响应
                val realtimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()
                if(realtimeResponse.status == "ok"&& dailyResponse.status=="ok"){
                    val weather = Weather(realtimeResponse.result.realtime,dailyResponse.result.daily)
                    Result.success(weather)
                }else{
                    Result.failure(RuntimeException("realtime response status is ${realtimeResponse.status}" +
                            "daily response status is ${dailyResponse.status}"))
                }
            }
//        catch (e:Exception){
//            Result.failure<Weather>(e)
//        }
//        emit(result)
    }

    //使用fire简化原来liveData的写法
    private fun <T> fire(context: CoroutineContext,block:suspend ()->Result<T>)= liveData<Result<T>>(context) {
        val result = try{
            block()
        }catch (e:Exception){
            Result.failure<T>(e)
        }
        emit(result)
    }

    fun savePlace(place:Place) = PlaceDao.savePlace(place)

    fun getSavedPlace()=PlaceDao.getSavedPlace()

    fun isPlaceSaved()=PlaceDao.isPlaceSaved()
}

