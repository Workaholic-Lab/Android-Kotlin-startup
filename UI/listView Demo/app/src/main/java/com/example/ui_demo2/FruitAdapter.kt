package com.example.ui_demo2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2020/10/09 20:32
 * @since: Kotlin 1.4
 * @modified by:
 */
class FruitAdapter(context: Context,val resourceId: Int, val data: MutableList<Fruit>) :
    ArrayAdapter<Fruit>(context, resourceId, data) {
    inner class  ViewHolder(val fruitImage:ImageView, val fruitName:TextView)
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view:View
            val viewHolder:ViewHolder
            if(convertView==null){
                view=LayoutInflater.from(context).inflate(resourceId,parent,false)
                val fruitImage: ImageView=view.findViewById(R.id.fruitImage)
                val fruitName:TextView=view.findViewById(R.id.fruitName)
                viewHolder=ViewHolder(fruitImage,fruitName)
                view.tag=viewHolder
            }else{
                view=convertView
                viewHolder=view.tag as ViewHolder
            }
            val fruit=getItem(position)//获取当前fruit实例
            if(fruit!=null){
                viewHolder.fruitImage.setImageResource(fruit.imageId)
                viewHolder.fruitName.text=fruit.name
            }
            return view
        }


}