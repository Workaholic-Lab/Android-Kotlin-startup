package com.workaholiclab.sunnyweather.logic.model

/**
 * @Description:
 * 将实时和每天的天气情况对象封装起来
 * @author: Gary
 * @date: Created on 2021/03/06 12:33
 * @since: Kotlin 1.4
 * @modified by:
 */
class Weather(val realtime: RealtimeResponse.Realtime,val daily: DailyResponse.Daily)