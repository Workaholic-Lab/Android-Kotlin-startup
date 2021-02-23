package com.workaholiclab.forcedoffline

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/02/22 10:15
 * @since: Kotlin 1.4
 * @modified by:
 */
open class BaseActivity:AppCompatActivity() {
    lateinit var receiver:ForceOfflineReceiver

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        ActivityCollector.addActivity(this)
    }

    override fun onResume() {
        super.onResume()
        val intentFilter=IntentFilter()
        intentFilter.addAction("com.workaholiclab.foeceoffline.FORCE_LINE")
        receiver=ForceOfflineReceiver()
        registerReceiver(receiver,intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }
    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    inner class ForceOfflineReceiver:BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            AlertDialog.Builder(context!!).apply {
                setTitle("Warning")
                setMessage("You are forced to be offline. Please try to login again.")
                setCancelable(false)
                setPositiveButton("OK"){_,_->
                    ActivityCollector.finishAll()
                    val i=Intent(context,LoginActivity::class.java)
                    context.startActivity(i)
                }
                show()
            }
        }

    }
}