# 基础UI界面设计

> 2021.2.16
>
> Gary哥哥的哥哥的哥哥

## 如何编写程序界面

> 通过编写XML的方式来实现
>
> * **不过Google推出的ConstraintLayout不是非常适合通过编写XML的方式来开发界面，而是适合在可视化编辑器下拖放控件来实现界面设计**
>   * **虽然Google现在更加倾向于让大家使用ConstraintLayout来开发程序界面**
>   * 但作为初学者，我们先对ConstraintLayout进行非常详细的讲解先。

> 下面是我用constraintLayout来编写的一个小小Demo，**详见Basic_UI_Demo的项目**

![](E:\kotlin-study\Studying-Kotlin\UI\UI_demo\constraint.png)

## 常用控件

## TextView



> ```kotlin
>  match_parent表示当前控件的大小和父布局的大小一样
>  wrap_content表示让当前控件的大小刚好能够包含住里面的全部内容
> ```

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
<!--    match_parent表示当前控件的大小和父布局的大小一样; wrap_content表示让当前控件的大小刚好能够包含住里面的全部内容-->

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</LinearLayout>
```

> ```xml
> android:gravity="center_vertical|center_horizontal"
> ```

|： 指定多个属性值

> ==距离用dp做单位，字体用sp做单位==

* 其他属性我就不一一介绍了，我们在实践时候自己去探索吧

## Button

```xml
 <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/button1"
        android:text="Button1"
        />
```

> 默认Button上的字母都是大写，可以通过属性修改
>
> * ```android:textAllCaps="false"```

> 下面通过kotlin来响应事件

```kotlin
package com.workaholiclab.control

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Lambda表达式实现
        button1.setOnClickListener {
            //逻辑代码
            Log.d("respond","button1 is clicked")
        }
    }
}
```



> 除了上面这种Lambda表达式来实现之外，还可以函数API的方式来注册监听器

```kotlin
package com.workaholiclab.control

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

//函数API来注册实现
class MainActivity : AppCompatActivity(),View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button2.setOnClickListener(this)
        
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.button2->{
                Log.d("respond2","button2 is clicked")
            }
        }
    }
}
```

* 上面这种写法我们让MainActivity实现了View.OnClickListener接口，并且重写了onClick方法，然后在调用button的setOnClickListener方法将Activity实例传递出去。

* 每当我们点击按钮的时候就会执行Onclick的方法

 

## EditText

```xml
    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/editText1"
        />
```

> hint属性可以提示信息

```xml
 <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/editText1"
        android:hint="Type something here"
        />
```

> 为了防止输入东西过多，导致EditText非常难看，我们用android:maxLines的属性
>
> * 下面代码表示EditText可以==显示==两行，以前的行不再显示，但还在的，往上滚了

```xml
<EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/editText1"
        android:hint="Type something here"
        android:maxLines="2"
        />
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Lambda表达式实现
        button1.setOnClickListener {
            //逻辑代码
            val input=editText1.text.toString()
            Toast.makeText(this,input,Toast.LENGTH_SHORT).show()
        }
    }
}
```



## ImageView

> src指定目录的图片

```xml
  <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:id="@+id/imageView1"
        android:src="@drawable/abc_vector_test"
        />
```

```kotlin
//手动更改
button2.setOnClickListener { 
            imageView1.setImageResource(R.drawable.ic_launcher_foreground)
        }
```



## ProgressBar

> ProgressBar是作用在界面上的一个进度条表示我们的程序正在加载一些数据

```xml
 <ProgressBar
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/progressBar"
        />
```

> 那么，我们当数据加载完之后如何让他消失掉呢
>
> * 我们利用控件的可视化来做

```kotlin
 button1.setOnClickListener {
            val input=editText1.text.toString()
            Toast.makeText(this,input,Toast.LENGTH_SHORT).show()
            if(progressBar.visibility==View.VISIBLE){
                progressBar.visibility=View.GONE
            }
            else{
                progressBar.visibility=View.VISIBLE
            }
        }
```



**我们还可以给ProgressBar指定样式**

> 默认为转圈圈

下面改成水平的：

```xml
  <ProgressBar
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/progressBar"
        style="@style/Widget.AppCompat.ProgressBar.Horizontal"
        android:max="100"
        />
```

```kotlin
//增加进度   
button1.setOnClickListener {
            val input=editText1.text.toString()
            Toast.makeText(this,input,Toast.LENGTH_SHORT).show()
            if(progressBar.visibility==View.VISIBLE){
                progressBar.visibility=View.GONE
            }
            else{
                progressBar.visibility=View.VISIBLE
                progressBar.progress=progressBar.progress+10
            }
        }
```



## AlertDialog

> 在当前用户见面弹出一个**对话窗**
>
> * 这个控件一般用于非常重要的内容或者是一些警告信息
>   * 下面通过kotlin代码来做一个Dialog
>   * 最后记得show出来

```kotlin
 button2.setOnClickListener {
            imageView1.setImageResource(R.drawable.ic_launcher_foreground)
            AlertDialog.Builder(this).apply {
                setTitle("This is a Dialog")
                setMessage("something Important")
                setCancelable(false)
                setPositiveButton("OK"){
                    dialog, which ->
                }
                setNegativeButton("Cancel"){
                    dialog, which ->
                }
                show()
            }
        }
```

![](E:\kotlin-study\Studying-Kotlin\UI\UI_demo\dialog.png)

> 空间部分我们先讲解那么多，其他控件及其属性可以通过实践来自行摸索



## 三种基本布局

### LinearLayout

> 线性布局是一种非常常见的布局，我们上面写的代码示例都是线性布局的

* 我们可以摄影水平和垂直的方向

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">
```

> 设置权重
>
> * 0dp代表不再有width的值来决定宽度
>   * 效果就是左右各一个

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="horizontal" android:layout_width="match_parent"
    android:layout_height="match_parent">

    <EditText
        android:id="@+id/ed1"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:hint="Type Someting"
        />
    <Button
        android:id="@+id/send"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="Send"/>
</LinearLayout>
```

> 更好的自适应效果：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="horizontal" android:layout_width="match_parent"
    android:layout_height="match_parent">

    <EditText
        android:id="@+id/ed1"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:hint="Type Someting"
        />
    <Button
        android:id="@+id/send"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Send"/>
</LinearLayout>
```

### RelativeLayout

> 相对布局也是比较常用的一种布局，与LinearLayout不同的是，它相对显得较为**随意**

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent" android:layout_height="match_parent">
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:id="@+id/button1"
    android:layout_alignParentLeft="true"
    android:layout_alignParentTop="true"
    />
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:id="@+id/button2"
    android:layout_alignParentRight="true"
    android:layout_alignParentTop="true"
    />
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:id="@+id/button3"
    android:layout_alignParentLeft="true"
    android:layout_alignParentBottom="true"
    />
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:id="@+id/button4"
    android:layout_alignParentRight="true"
    android:layout_alignParentBottom="true"
    />
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:id="@+id/button5"
    android:layout_centerInParent="true"
    />
</RelativeLayout>
```

![](E:\kotlin-study\Studying-Kotlin\UI\UI_demo\relative.png)

> 还可以相对于其他控件进行布局

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent" android:layout_height="match_parent">
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:id="@+id/button1"
    android:layout_below="@+id/button5"
    android:layout_toRightOf="@+id/button5"
    />
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:id="@+id/button2"
    android:layout_below="@+id/button5"
    android:layout_toLeftOf="@+id/button5"
    />
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:id="@+id/button3"
    android:layout_above="@+id/button5"
    android:layout_toRightOf="@+id/button5"
    />
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:id="@+id/button4"
    android:layout_above="@+id/button5"
    android:layout_toLeftOf="@+id/button5"
    />
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:id="@+id/button5"
    android:layout_centerInParent="true"
    />
</RelativeLayout>
```

> 相对于中间那个Button来进行布局



### FrameLayout

> 帧布局，这种布局是非常非常简单的同时它的引用场景确实也比较少
>
> * 不过在后面的Fragment的介绍当中还会有用处的
>   * 下面就是一左一右：

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent" android:layout_height="match_parent">
<TextView
    android:id="@+id/tv1"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="left"
    android:text="This is a TextView"
    />
    
    <Button
        android:id="@+id/bt1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="right"
        android:text="Button"/>
</FrameLayout>
```



## 创建自定义的控件

### 引入布局

> 在系统控件不够用的时候，还可以创建自定义的控件
>
> 其实那些控件继承都是：TextView 或者 ImageView 或者 ViewGroup**（准确点说 EditText，Button继承TextView，其他都是继承ViewGroup）**
>
> * 而他们的祖先都是View
>   * **这里做一个类似于一起IOS风格的布局我就不做展示了，可以见书本P169的内容**
>   * ***一个xml布局，多个activity共用***
>   * Button可以设置backGround属性来加入自己喜欢的形状

> 当我们写了一个frame.xml的布局之后，想在其他layout当中调用:

```xml
<include layout="@layout/frame"/>
```

```kotlin
//隐藏标题栏
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
    }
}
```



### 自定义控件

> 我们创建TitleLayout继承LinearLayout
>
> * 这里我们以自定义一个标题栏控件
>   * ==**与添加普通控件时候略有不同的是，添加自定义控件的时候，完整的类名和包名是不可省略的**==

```kotlin
class TitleLayout(context:Context,attrs:AttributeSet):LinearLayout(context,attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.test,this)
    }
}
```

在布局中引入TitileLayout会调用这个构造函数，在init结构体中会进行动态加载，借助LayoutInflater来实现

* from()方法构建出一个LayoutInflater对象
* inflate()动态加载一个布局文件
  * 第一个参数是要加载的布局文件id
  * 第二个参数是加载好的布局再添加一个布局

**现在自定义控件已经创建好了，接下来我们需要在布局文件中添加这个自定义控件**

> 修改main_activity.xml：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

   <com.workaholiclab.manifcontrol.TitleLayout
       android:layout_width="match_parent"
       android:layout_height="wrap_content"/>

</LinearLayout>
```

test.xml如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent" android:layout_height="match_parent">
    <Button
        android:id="@+id/button1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="left"
        android:text="Back"/>

    <Button
        android:id="@+id/button2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="right"
        android:text="Edit"/>
</FrameLayout>
```

```kotlin
class TitleLayout(context:Context,attrs:AttributeSet):LinearLayout(context,attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.test,this)
        button1.setOnClickListener { val activity=context as Activity
        activity.finish()
        }
        button2.setOnClickListener { Toast.makeText(context,"you clicked Edit button",Toast.LENGTH_SHORT).show() }
    }
}
```



```
button1.setOnClickListener { val activity=context as Activity
activity.finish()
```

这里TitleLayout中接受的context参数实际上是一个Activity的实例，类型转化为Activity，然后调用finish()方法

* as 为强制类型转换



## 最难的控件：ListView

> 数据滚动出屏幕
>
> * ex: QQ聊天记录， 翻看微博的信息.....

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <ListView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/lv1"/>
</LinearLayout>
```

```kotlin
class MainActivity : AppCompatActivity() {
    private val data= listOf<String>("Apple","Banana","Pear","Watermelon","Grape","Pineapple","Strawberry","Cherry","Mango","Orange")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data)
        lv1.adapter=adapter
    }
}
```

> **下面这段代码就是关键**
>
> * 把Adapter构建好：```val adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data)```
> * 将适配好的Adapter对象传递给listView的

```kotlin
val adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data)
lv1.adapter=adapter
```



### 自定义ListView

> 我们给单调的文字稍微加一点图片吧！！！

* 定义一个实体类 Fruit 和fruit_item.xml

```kotlin
class Fruit(val name:String,val imageId:Int) {
    
}
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="60dp">
    
    <ImageView
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_gravity="center_vertical"
        android:layout_marginLeft="10dp"
        android:id="@+id/fruitImage"
        />
    
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_vertical"
        android:layout_marginLeft="10dp"
        android:id="@+id/fruitName"/>

</LinearLayout>
```



* 新建FruitAdapter

  > ==这次Adapt是这个Fruit的对象了==
  >
  > * 自定义适配器！！！
  > * 很重要哦

  ```kotlin
  class FruitAdapter(activity: Activity,val resourceId:Int, data:List<Fruit>): ArrayAdapter<Fruit>(activity,resourceId,data) {
      override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
          val view =LayoutInflater.from(context).inflate(resourceId,parent,false)
          val fruitImage:ImageView=view.findViewById(R.id.fruitImage)
          val fruitName:TextView=view.findViewById(R.id.fruitName)
          val fruit=getItem(position)//获取当前项的Fruit实例
          fruit?.let { fruitImage.setImageResource(fruit.imageId)
          fruitName.text=fruit.name}
          return view
      }
  }
  ```

> 说明：
>
> * LayoutInflater的inflate()接受的三个参数，前面两个我们已经知道是怎么回事了:
>   * 第一个参数是要加载的布局文件id
>   * 第二个参数是加载好的布局再添加一个布局
>   * **第三个参数false就是表示只让我们在父布局中声明的layout失效，==但不会为这个View添加父布局==**
> * 这就是ListView**标准**的写法

* 加入实例
  * repeat函数，后面跟一个Lambda表达式：

```kotlin
repeat(2){
    for (fn in data)
    {
        fruitList.add(Fruit(fn,R.drawable.ic_launcher_background))
    }
```

```kotlin
package com.workaholiclab.listviewtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val fruitList=ArrayList<Fruit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFruits()
        val adapter=FruitAdapter(this,R.layout.fruit_item,fruitList)
        lv1.adapter=adapter
//        val adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data)
//        lv1.adapter=adapter
    }

    private fun initFruits() {
        val data= listOf<String>("Apple","Banana","Pear","Watermelon","Grape","Pineapple","Strawberry","Cherry","Mango","Orange")
        repeat(2){
            for (fn in data)
            {
                fruitList.add(Fruit(fn,R.drawable.ic_launcher_background))
            }
        }
    }
}
```

> 上面的关于listView的全部代码，可能有的部分需要自行做一些小小修改
>
> * 我这里没有这些水果的图片我随便放一张图片上去就算了

### 提升ListView的效率

> ListView之所以难用，因为它很多的细节都是可以优化的，运行效率就是其中的一个优化点
>
> * 效率低的原因是getView()方法中每次都要将布局重新加载一遍，当其快速滚动的时候，其性能就会成为瓶颈
> * **我们使用convertView参数来进行缓存！！！**

#### 重写getView()方法

```kotlin
class FruitAdapter(activity: Activity,val resourceId:Int, data:List<Fruit>): ArrayAdapter<Fruit>(activity,resourceId,data) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view:View = convertView ?: LayoutInflater.from(context).inflate(resourceId,parent,false)
        val fruitImage:ImageView=view.findViewById(R.id.fruitImage)
        val fruitName:TextView=view.findViewById(R.id.fruitName)
        val fruit=getItem(position)//获取当前项的Fruit实例
        fruit?.let { fruitImage.setImageResource(fruit.imageId)
        fruitName.text=fruit.name}
        return view
    }
}
```

#### 继续优化

> **现在每次在getView方法中仍然还是会调用View的findViewById的方法来获取一次控件的实例**
>
> * 现在我们借助一个ViewHolder来对这一部分进行优化
> * 使用内部内inner class实现所有控件的缓存都放在ViewHolder中，没有必要每次findViewById()

```kotlin
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
```

> 经过上述两步的优化后，性能已经非常不错了

#### ListView的点击事件

> 为ListView注册一个监听器即可

```kotlin
class MainActivity : AppCompatActivity() {
    private val fruitList=ArrayList<Fruit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFruits()
        val adapter=FruitAdapter(this,R.layout.fruit_item,fruitList)
        lv1.adapter=adapter
        lv1.setOnItemClickListener { parent, view, position, id ->
            val fruit=fruitList[position]
            Toast.makeText(this,fruit.name,Toast.LENGTH_SHORT).show()
        }
//        val adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data)
//        lv1.adapter=adapter
    }
    private fun initFruits() {
        val data= listOf<String>("Apple","Banana","Pear","Watermelon","Grape","Pineapple","Strawberry","Cherry","Mango","Orange")
        repeat(2){
            for (fn in data)
            {
                fruitList.add(Fruit(fn,R.drawable.ic_launcher_background))
            }
        }
    }
}
```

> 没有用到的参数Kotlin还允许用下划线_代替

```kotlin
lv1.setOnItemClickListener { _, _, position, _ ->
    val fruit=fruitList[position]
    Toast.makeText(this,fruit.name,Toast.LENGTH_SHORT).show()
}
```



## RecyclerView

> 更加强大的滚动组件
>
> * 他是一个增强版本的ListView，未来的更多程序会从ListView转向RecyclerView

### 基本用法

```gradle
implementation 'androidx.recyclerview:recyclerview:1.0.0'
```

> 具体到最新版本可以自行查找

> 下面是RecyclerView适配器的标准写法

```kotlin
class FruitAdapter(val fruitList:List<Fruit>):RecyclerView.Adapter<FruitAdapter.ViewHolder>() {
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val fruitImage: ImageView =view.findViewById(R.id.fruitImage)
        val fruitName: TextView=view.findViewById(R.id.fruitName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.fruit_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return fruitList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fruit=fruitList[position]
        holder.fruitImage.setImageResource(fruit.imageId)
        holder.fruitName.text=fruit.name
    }
}
```

> 上面这个RecyclerView适配器的写法看起来好复杂，里面的方法很多，但其实比ListView更加简洁
>
> * 首先我们定义一个内部内ViewHolder，它继承自RecyclerView.ViewHolder
> * 然后ViewHoder的主构造函数中要传入一个View的参数
>   * 这个参数是RecyclerView子项的最外层布局
>   * 可以通过findViewById的方法获取布局中的IamgeView和TextView实例
> * FruitAdapter中有一个主构造函数，```FruitAdapter(val fruitList:List<Fruit>)```他用于把展示的数据传进来，方便我们后续操作
> * 重写父类```RecyclerView.Adapter<FruitAdapter.ViewHolder>```的三个方法：
>   * onCreateViewHolder()创建一个ViewHolder实例，**并把加载出来的布局传到构造函数里面**，最后将ViewHolder的实例返回
>   * onBindViewHolder()方法用于对RecyclerView子项的数据进行赋值，会在每个子项被滚动到屏幕内的时候执行，这里我们通过position参数得到当前项的Fruit实例，然后再设置到ViewHolder中
>   * getItemCount方法就是返回多少个子项就好了

适配器准备好之后，我们使用RecyclerView如下：

```kotlin
class MainActivity : AppCompatActivity() {
    private val fruitList=ArrayList<Fruit>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFruits()
        val layoutManager=LinearLayoutManager(this)
        rv1.layoutManager=layoutManager
        val adapter=FruitAdapter(fruitList)
        rv1.adapter=adapter
    }
    private fun initFruits() {
        val data= listOf<String>("Apple","Banana","Pear","Watermelon","Grape","Pineapple","Strawberry","Cherry","Mango","Orange")
        repeat(2){
            for (fn in data)
            {
                fruitList.add(Fruit(fn,R.drawable.ic_launcher_background))
            }
        }
    }
}
```

> * 先创建LinearLayoutManager对象，并把它设置到RecyclerView
> * LayoutManager指定RecyclerView的布局方式
> * 最后调用RecyclerView的setAdapter方法来完成调用即可

### 实现横向布局与瀑布布局

> 要想实现横向滚动的话就修改一下fruit_item.xml的代码

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="80dp"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <ImageView
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="10dp"
        android:id="@+id/fruitImage"
        />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="10dp"
        android:id="@+id/fruitName"/>

</LinearLayout>
```



```kotlin
class MainActivity : AppCompatActivity() {
    private val fruitList=ArrayList<Fruit>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFruits()
        val layoutManager=LinearLayoutManager(this)
        rv1.layoutManager=layoutManager
        layoutManager.orientation=LinearLayoutManager.HORIZONTAL
        val adapter=FruitAdapter(fruitList)
        rv1.adapter=adapter
    }
    private fun initFruits() {
        val data= listOf<String>("Apple","Banana","Pear","Watermelon","Grape","Pineapple","Strawberry","Cherry","Mango","Orange")
        repeat(2){
            for (fn in data)
            {
                fruitList.add(Fruit(fn,R.drawable.ic_launcher_background))
            }
        }
    }
}
```

> **加多了一行```layoutManager.orientation=LinearLayoutManager.HORIZONTAL```即可，默认是纵向的！**

> LayoutManager就是RecyclerView的最大优势所在
>
> * 制定了一套可扩展的布局排列接口，子类只要按照接口的规范来实现，就能定制出不同的排列布局了

> 下面我们来看看瀑布布局

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="5dp"
    android:orientation="vertical">

    <ImageView
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="10dp"
        android:id="@+id/fruitImage"
        />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="left"
        android:layout_marginTop="10dp"
        android:id="@+id/fruitName"/>

</LinearLayout>
```

```kotlin
class MainActivity : AppCompatActivity() {
    private val fruitList=ArrayList<Fruit>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFruits()
        val layoutManager=StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
        rv1.layoutManager=layoutManager
        val adapter=FruitAdapter(fruitList)
        rv1.adapter=adapter
    }
    private fun initFruits() {
        val data= listOf<String>("Apple","Banana","Pear","Watermelon","Grape","Pineapple","Strawberry","Cherry","Mango","Orange")
        repeat(2){
            for (fn in data)
            {
                fruitList.add(Fruit(getRandomLengthString(fn),R.drawable.ic_launcher_background))
            }
        }
    }

    private fun getRandomLengthString(str: String): String {
        val n=(1..20).random()
        val builder=StringBuilder()
        repeat(n){
            builder.append(str)
        }
        return builder.toString()
    }
}
```



> 核心代码如下：

```kotlin
        val layoutManager=StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
        rv1.layoutManager=layoutManager
        val adapter=FruitAdapter(fruitList)
        rv1.adapter=adapter
```

> 效果图：



![](E:\kotlin-study\Studying-Kotlin\UI\瀑布布局.png)



### 点击事件

> 这一点和ListView相比，比较特殊，它没有方法实现，**需要在自己给子项具体的View去注册点击事件**
>
> * 这里相对于ListView来说确实要复杂一些

**修改FruitAdapter中==onCreateViewHolder==的代码**

```kotlin
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.fruit_item,parent,false)
        val viewHolder=ViewHolder(view)
        viewHolder.itemView.setOnClickListener{
            val position=viewHolder.adapterPosition
            val fruit=fruitList[position]
            Toast.makeText(parent.context,"you clicked view ${fruit.name}",Toast.LENGTH_SHORT).show()
        }
        viewHolder.fruitImage.setOnClickListener{
            val position=viewHolder.adapterPosition
            val fruit=fruitList[position]
            Toast.makeText(parent.context,"you clicked view ${fruit.name}",Toast.LENGTH_SHORT).show()
        }
        return viewHolder
    }
```

> 注意最够返回的，这里比较麻烦的是图片和文字都要响应这个点击事件



## 编写界面的最佳实践

> ==这一部分主要内容自己看书本P193页开始吧==
>
> * 这里我们也来讲解一下
> * 编写一个精美的聊天界面

### 制作9-Patch图片

> **这是一种经过特殊处理的png图片，能够制定那个区域被拉伸，哪些区域不可以被拉伸**

*在对应的资源图片右击学Create 9-Patch file即可*

* 黑色部分表示可以拉伸
* 按住Shift+鼠标可以擦除
* ==最后记得把原来那张图片删除就好了==
  * 因为android是不允许有两张的，即使后缀名不同也不可以

![](E:\kotlin-study\Studying-Kotlin\UI\9patch.png)





修改activity_main.xml代码：

```xml
android:background="#d8e0e8"
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#d8e0e8" >

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content" >

        <EditText
            android:id="@+id/inputText"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:hint="Type something here"
            android:maxLines="2" />

        <Button
            android:id="@+id/send"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Send" />

    </LinearLayout>

</LinearLayout>
```



### 利用RecyclerView来制作

> 具体导入这里就不再做过多的阐述了

* 新建Msg实体类

  ```kotlin
  class Msg(val content: String, val type: Int) {
      companion object {
          const val TYPE_RECEIVED = 0 
          const val TYPE_SENT = 1
      }
  }
  ```

  > 只有在单例类，顶层方法，companion object中才可以使用const关键字

  **接下来开始写RecyclerView的子布局了**

  > 左右各一个

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
      android:orientation="vertical"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      android:padding="10dp" >
  
      <LinearLayout
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:layout_gravity="left"
          android:background="@drawable/message_left" >
  
          <TextView
              android:id="@+id/leftMsg"
              android:layout_width="wrap_content"
              android:layout_height="wrap_content"
              android:layout_gravity="center"
              android:layout_margin="10dp"
              android:textColor="#fff" />
  
      </LinearLayout>
  
  </FrameLayout>
  ```

  

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="10dp" >

    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="right"
        android:background="@drawable/message_right" >

        <TextView
            android:id="@+id/rightMsg"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:layout_margin="10dp"
            android:textColor="#000" />

    </LinearLayout>


</FrameLayout>
```

**Adapter的代码也是差不多：**

```kotlin
package com.example.uibestpractice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class MsgAdapter(val msgList: List<Msg>) : RecyclerView.Adapter<MsgViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        val msg = msgList[position]
        return msg.type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if (viewType == Msg.TYPE_RECEIVED) {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_left_item, parent, false)
        LeftViewHolder(view)
    } else {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_right_item, parent, false)
        RightViewHolder(view)
    }

    override fun onBindViewHolder(holder: MsgViewHolder, position: Int) {
        val msg = msgList[position]
        when (holder) {
            is LeftViewHolder -> holder.leftMsg.text = msg.content
            is RightViewHolder -> holder.rightMsg.text = msg.content
         }
    }

    override fun getItemCount() = msgList.size

}
```

**上面的代码我们根据不同的ViewType创建不同的界面**

我们使用封闭类来实现：

```kotlin
sealed class MsgViewHolder(view: View) : RecyclerView.ViewHolder(view)

class LeftViewHolder(view: View) : MsgViewHolder(view) {
    val leftMsg: TextView = view.findViewById(R.id.leftMsg)
}

class RightViewHolder(view: View) : MsgViewHolder(view) {
    val rightMsg: TextView = view.findViewById(R.id.rightMsg)
}
```

* 发送点击事件就很简单了

```kotlin
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val msgList = ArrayList<Msg>()

    private lateinit var adapter: MsgAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMsg()
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        if (!::adapter.isInitialized) {
            adapter = MsgAdapter(msgList)
        }
        recyclerView.adapter = adapter
        send.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            send -> {
                val content = inputText.text.toString()
                if (content.isNotEmpty()) {
                    val msg = Msg(content, Msg.TYPE_SENT)
                    msgList.add(msg)
                    adapter.notifyItemInserted(msgList.size - 1) // 当有新消息时，刷新RecyclerView中的显示
                    recyclerView.scrollToPosition(msgList.size - 1)  // 将 RecyclerView定位到最后一行
                    inputText.setText("") // 清空输入框中的内容
                }
            }
        }
    }

    private fun initMsg() {
        val msg1 = Msg("Hello guy.", Msg.TYPE_RECEIVED)
        msgList.add(msg1)
        val msg2 = Msg("Hello. Who is that?", Msg.TYPE_SENT)
        msgList.add(msg2)
        val msg3 = Msg("This is Tom. Nice talking to you. ", Msg.TYPE_RECEIVED)
        msgList.add(msg3)
    }
}
```

> 这里你喜欢用另外一种风格的来写也是可以的

效果如下：

![](E:\kotlin-study\Studying-Kotlin\UI\bestpractice.png)



* 再补充一个封闭内

```kotlin
sealed class Result

class Success(val msg: String) : Result()

class Failure(val error: Exception) : Result()


fun getResultMsg(result: Result) = when (result) {
    is Success -> result.msg
    is Failure -> "Error is ${result.error.message}"
}
```

> UI基础设计部分正式完工