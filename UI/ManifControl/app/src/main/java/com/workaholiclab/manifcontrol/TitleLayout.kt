package com.workaholiclab.manifcontrol

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.test.view.*

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/02/17 9:42
 * @since: Kotlin 1.4
 * @modified by:
 */
class TitleLayout(context:Context,attrs:AttributeSet):LinearLayout(context,attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.test,this)
        button1.setOnClickListener { val activity=context as Activity
        activity.finish()
        }
        button2.setOnClickListener { Toast.makeText(context,"you clicked Edit button",Toast.LENGTH_SHORT).show() }
    }
}