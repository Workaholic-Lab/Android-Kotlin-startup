package com.workaholiclab.bestparcticefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.news_title_frag.*
import java.lang.StringBuilder

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/02/20 10:07
 * @since: Kotlin 1.4
 * @modified by:
 */
class NewsTitleFragment:Fragment(){
    private var isTwopane=false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.news_title_frag,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isTwopane=activity?.findViewById<View>(R.id.newsContentLayout)!=null
        val layoutManager=LinearLayoutManager(activity)
        newsTitleRecyclerView.layoutManager=layoutManager
        val adapter=NewsAdapter(getNews())
        newsTitleRecyclerView.adapter=adapter
    }

    private fun getNews(): List<News> {
        val newsList=ArrayList<News>()
        for(i in 1..50){
            val news=News("This is news title $i",getRandomLengthString("This is news content $i."))
            newsList.add(news)
        }
        return newsList
    }

    private fun getRandomLengthString(str: String): String {
        val n=(1..20).random()
        val builder=StringBuilder()
        repeat(n){
            builder.append(str)
        }
        return builder.toString()
    }

    inner class NewsAdapter(val newsList: List<News>):RecyclerView.Adapter<NewsAdapter.ViewHolder>(){
        inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
            val newsTitle:TextView=view.findViewById(R.id.newsTitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view=LayoutInflater.from(parent.context).inflate(R.layout.news_item,parent,false)
            val holder=ViewHolder(view)
            holder.itemView.setOnClickListener{
                val news=newsList[holder.adapterPosition]
                if (isTwopane)
                {
                    val fragment=newsContentFrag as NewsContentFragment
                    fragment.refresh(news.title,news.content)
                }
                else{
                    NewsContentActivity.actionStart(parent.context,news.title,news.content)
                }
            }
            return holder
        }

        override fun getItemCount(): Int {
            return newsList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val news=newsList[position]
            holder.newsTitle.text=news.title
        }
    }
}