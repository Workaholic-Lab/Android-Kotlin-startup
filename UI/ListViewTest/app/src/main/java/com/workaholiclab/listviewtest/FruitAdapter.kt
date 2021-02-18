package com.workaholiclab.listviewtest

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/02/18 18:40
 * @since: Kotlin 1.4
 * @modified by:
 */
class FruitAdapter(activity: Activity,val resourceId:Int, data:List<Fruit>): ArrayAdapter<Fruit>(activity,resourceId,data) {
    inner class ViewHolder(val fruitImageView: ImageView,val fruitName: TextView)
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val viewHolder:ViewHolder
            val view:View
            if (convertView==null){
                view=LayoutInflater.from(context).inflate(resourceId,parent,false)
                val fruitImage:ImageView=view.findViewById(R.id.fruitImage)
                val fruitName:TextView=view.findViewById(R.id.fruitName)
                viewHolder=ViewHolder(fruitImage,fruitName)
                view.tag=viewHolder
            }else{
                view=convertView
                viewHolder=view.tag as ViewHolder
            }
            val fruit=getItem(position)//获取当前项的Fruit实例
            fruit?.let {
                viewHolder.fruitImageView.setImageResource(fruit.imageId)
                viewHolder.fruitName.text=fruit.name
            }
            return view
        }

}