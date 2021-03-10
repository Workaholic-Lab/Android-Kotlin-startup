package com.workaholiclab.sunnyweather.logic.network

import kotlinx.coroutines.suspendAtomicCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * @Description:
 * 统一的网络数据源访问入口，对API进行封装
 * @author: Gary
 * @date: Created on 2021/03/06 10:17
 * @since: Kotlin 1.4
 * @modified by:
 */
object SunnyWeatherNetwork {

    private val weatherService = ServiceCreator.create(WeatherService::class.java)

    suspend fun getDailyWeather(lng:String,lat:String)= weatherService.getDailyWeather(lng,lat).await()
    suspend fun getRealtimeWeather(lng:String,lat:String)= weatherService.getRealtimeWeather(lng,lat).await()



    //创建一个动态代理对象，以便发起搜索城市请求
    private val placeService = ServiceCreator.create(PlaceService::class.java)

    //发起网络请求,成功或者失败后返回
    suspend fun searchPlaces(query:String) = placeService.searchPlaces(query).await()//searchPlaces返回Call<PlaceResponse>

    //挂起函数
     private suspend fun <T> Call<T>.await(): T {
        //当前协程挂起，普通线程执行Lambda表达式
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                //对异常进行处理
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

                //得到服务器返回数据
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        //恢复挂起的协程
                        continuation.resume(body)
                    } else {
                        continuation.resumeWithException(RuntimeException("response body is null"))
                    }
                }
            })
        }
    }
}