package com.example.fragment_demo2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2020/10/16 0:12
 * @since: Kotlin 1.4
 * @modified by:
 */
class TestFragment :Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            return inflater.inflate(R.layout.test_fragment,container,false)
    }
}