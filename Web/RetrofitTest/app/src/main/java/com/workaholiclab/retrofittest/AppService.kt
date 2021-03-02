package com.workaholiclab.retrofittest

import retrofit2.Call
import retrofit2.http.GET

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/02 21:23
 * @since: Kotlin 1.4
 * @modified by:
 */
interface AppService {
    @GET("get_data.json")
    fun getAppData():Call<List<App>>
}