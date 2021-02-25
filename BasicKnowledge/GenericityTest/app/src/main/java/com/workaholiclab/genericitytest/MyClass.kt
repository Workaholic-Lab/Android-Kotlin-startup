package com.workaholiclab.genericitytest

import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat

val channelId: String?=null
val context:Context?=null
/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/02/24 23:02
 * @since: Kotlin 1.4
 * @modified by:
 */
val notification=NotificationCompat.Builder(context,channelId).setContentTitle("This is content title")
    .setContentText("This is content text").setSmallIcon(R.drawable.ic_launcher_background)
    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_background))
    .build()
