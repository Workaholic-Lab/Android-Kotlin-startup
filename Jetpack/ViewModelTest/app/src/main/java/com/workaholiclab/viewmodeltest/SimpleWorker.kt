package com.workaholiclab.viewmodeltest

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/03/05 20:06
 * @since: Kotlin 1.4
 * @modified by:
 */
class SimpleWorker(context: Context,params:WorkerParameters):Worker(context,params) {
    override fun doWork(): Result {
        Log.d("SimpleWorker","do work in SimpleWorker")
        return Result.success()
    }

}