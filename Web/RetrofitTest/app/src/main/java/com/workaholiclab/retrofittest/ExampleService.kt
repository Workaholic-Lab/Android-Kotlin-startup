package com.workaholiclab.retrofittest

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/03 9:28
 * @since: Kotlin 1.4
 * @modified by:
 */
interface ExampleService {
    @GET("{page}/get_data_json")
    fun getData1(@Path("page")page:Int): Call<Data>
    @GET("get_data.json")
    fun getData2(@Query("u")user:String,@Query("t")token:String):Call<Data>

    @DELETE("data/{id}")
    fun deleteData(@Path("id")id:String):Call<ResponseBody>

    @POST("data/create")
    fun postData(@Body data:Data):Call<ResponseBody>

    @Headers("User-Agent:okhttp","Cache-Control:max-age=0")
    @GET("get_data.json")
    fun getData3():Call<Data>

    @GET("get_data.json")
    fun getData4(@Header("User-Agent")userAgent:String,
                 @Header("Cache-Control")cacheControl:String):Call<Data>
}