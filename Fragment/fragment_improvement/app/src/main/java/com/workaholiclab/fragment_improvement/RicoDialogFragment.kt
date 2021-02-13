package com.workaholiclab.fragment_improvement

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2020/11/05 17:02
 * @since: Kotlin 1.4
 * @modified by:
 */
class RicoDialogFragment:DialogFragment() {

    fun  getInstance(type:Int):RicoDialogFragment{
        val dialog:RicoDialogFragment= RicoDialogFragment()
        val bundle:Bundle=Bundle()
        bundle.putInt("Dialog_Type",type)
        return dialog
    }

    /**
     * 创建一个dialog并且返回
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog:Dialog
        val dialogType=arguments.getInt("Dialog_Type")
        when(dialogType){

        }
        return super.onCreateDialog(savedInstanceState)
    }
}