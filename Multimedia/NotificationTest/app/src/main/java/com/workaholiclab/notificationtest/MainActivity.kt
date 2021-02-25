package com.workaholiclab.notificationtest

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel = NotificationChannel("normal","Normal",NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
            val channel2=NotificationChannel("important","Important",NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel2)
        }
        sendNotice.setOnClickListener {
            //构建意图
            val intent=Intent(this,NotificationActivity::class.java)
            val pi = PendingIntent.getActivity(this,0,intent,0)

            //注意下面setContentIntent(pi)
            //setAutoCancel(true) 点击之后自动消失
            val notification = NotificationCompat.Builder(this,"important")
                .setContentTitle("This is content title")
//                .setContentText("　全国脱贫攻坚总结表彰大会开始，中共中央政治局常委、全国政协主席汪洋同志宣读《中共中央 国务院关于授予全国脱贫攻坚楷模荣誉称号的决定》。\n" +
//                        "\n" +
//                        "　　为隆重表彰激励先进，大力弘扬民族精神、时代精神和脱贫攻坚精神，充分激发全党全国各族人民干事创业的责任感、使命感、荣誉感，汇聚更强大的力量推进全面建设社会主义现代化国家，党中央、国务院决定，授予毛相林等10名同志，河北省塞罕坝机械林场等10个集体“全国脱贫攻坚楷模”荣誉称号。")
//                .setStyle(NotificationCompat.BigTextStyle().bigText("　全国脱贫攻坚总结表彰大会开始，中共中央政治局常委、全国政协主席汪洋同志宣读《中共中央 国务院关于授予全国脱贫攻坚楷模荣誉称号的决定》。\n" +
//                        "\n" +
//                        "　　为隆重表彰激励先进，大力弘扬民族精神、时代精神和脱贫攻坚精神，充分激发全党全国各族人民干事创业的责任感、使命感、荣誉感，汇聚更强大的力量推进全面建设社会主义现代化国家，党中央、国务院决定，授予毛相林等10名同志，河北省塞罕坝机械林场等10个集体“全国脱贫攻坚楷模”荣誉称号。"))
                .setContentText("Look, what a beautiful picture")
                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(resources,R.drawable.ic_launcher_background)))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_launcher_background))
                .setContentIntent(pi)
//                .setAutoCancel(true)
                .build()
            manager.notify(1,notification)
        }
    }
}