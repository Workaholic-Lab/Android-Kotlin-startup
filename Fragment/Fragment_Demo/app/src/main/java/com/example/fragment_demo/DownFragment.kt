package com.example.fragment_demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2020/10/13 22:27
 * @since: Kotlin 1.4
 * @modified by:
 */
class DownFragment:Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View=inflater.inflate(R.layout.down_fragment,container,false)
        return view
    }
}