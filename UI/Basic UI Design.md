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

![](E:\kotlin-study\Studying-Kotlin\UI_demo\constraint.png)

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

![](E:\kotlin-study\Studying-Kotlin\UI_demo\dialog.png)

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

![](E:\kotlin-study\Studying-Kotlin\UI_demo\relative.png)

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



## 最难的空间：ListView

> 数据滚动出屏幕
>
> * ex: QQ聊天记录， 翻看微博的信息.....

