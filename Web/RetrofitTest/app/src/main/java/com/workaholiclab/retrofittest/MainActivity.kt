package com.workaholiclab.retrofittest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private val mkey ="MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getAppData.setOnClickListener {

            //val retrofit = Retrofit.Builder().baseUrl("http://10.0.2.2/").addConverterFactory(GsonConverterFactory.create()).build()
            val appService = ServiceCreator.create(AppService::class.java)
            appService.getAppData().enqueue(object : Callback<List<App>>{
                override fun onFailure(call: Call<List<App>>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<List<App>>, response: Response<List<App>>) {
                    val list =response.body()
                    if(list!=null){
                        for (app in list){
                            Log.d(mkey,"id is ${app.id}")
                            Log.d(mkey,"name is ${app.name}")
                            Log.d(mkey,"version is ${app.version}")
                        }
                    }
                }

            })
        }
    }
}