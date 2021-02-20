package com.workaholiclab.bestparcticefragment

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_news_content.*
import kotlinx.android.synthetic.main.news_content_frag.*

class NewsContentActivity : AppCompatActivity() {
    companion object{
        fun actionStart(context: Context,title:String,content:String){
            val intent=Intent(context,NewsContentActivity::class.java).apply {
                putExtra("news_title",title)
                putExtra("news_content",content)
            }
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_content)
        val title=intent.getStringExtra("news_title")
        val content=intent.getStringExtra("news_content")
        if (title!=null&&content!=null)
        {
            val fragment=newsContentFrag as NewsContentFragment
            fragment.refresh(title,content)//刷新NewsContentFragment界面
        }
    }
}