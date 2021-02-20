package com.workaholiclab.bestparcticefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.news_content_frag.*

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/02/20 9:43
 * @since: Kotlin 1.4
 * @modified by:
 */
class NewsContentFragment:Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      return  inflater.inflate(R.layout.news_content_frag,container,false)

    }

    fun refresh(title:String,content:String)
    {
        contentLayout.visibility=View.VISIBLE
        //刷新内容
        newsContent.text=content
        newsTitle.text=title
    }
}