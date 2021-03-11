# Activity 的生命周期

> 2021.1.14 
>
> Gary Chen

## Activity被回收了怎么办

> 如果A被回收掉了，从B返回A后，仍然可以显示A，但是不会知心onRestart()方法，==而是执行A的onCreate()的方法==，相当于A重新创建了一次

**onSaveInstanceState()回调方法**，在回收之前被调用，对临时数据进行保存：

```kotlin
  override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        val tempData="Something you just typed"
        outState.putString("data_key",tempData)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        //一般这个Bundle都是空的，现在不是空了！！！
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first_layout)
        if (savedInstanceState!=null){
            val tempData=savedInstanceState.getString("data_key")
        }
    }
```

> 可以先将数据保存在Bundle对象中，再将这个对象存放到Intent中。来到目标中再将数据一一去出
>
> * 需要注意的是，在横竖屏转化的过程当中，会调用onCreate()的方法，但不推荐用上面的方法来解决，我们后面的章节会降到更好更加优雅的解决方法。

# Activity的启动模式

> 在AndroidMainifest.xml 文件当中修改 android: launchMode 属性即可

## standard

> Android的==默认==启动模式

其采用的数据结构就是一个简单的返回栈，**==先进后出==**,同一个栈顶Activity可以多次创建，不会影响



## singleTop

> ==栈顶Activity只会创建一个==，但若未处于栈顶还是会创建的！！！



## singleTask

> 这个模式就可以解决上面两个Mode所出现的一些问题，Activity只会创建一次



## singleInstance

> 最特殊的启动模式，也是最复杂的启动模式
>
> * 指定为singleInstance模式的Activity会启动一个==新的返回栈==来管理这个Activity
>   * 当我们有一个Activity是允许被其他程序调用的，实现其他程序和我们的程序==共享==这个Activity实例



# Activity的最佳实现

## 知晓当前是哪一个Activity

```kotlin
class MainActivity : AppCompatActivity() {    
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity",javaClass.simpleName) //Activity创建时候，打印日志，知晓当前Activity
    }
}
```

```kotlin
BaseActivity:class.java相当于java语言当中的BaseActivity.class来获取BaseActivity类的对象   javaClass默认为获取当前Activity实例的对象
```



## 随时随地退出程序

> 新建一个==单例类ActivityCollector==作为Activity的集合来实现

```kotlin
package com.example.activity_test

import android.app.Activity

object ActivityCollector {
    private val activities=ArrayList<Activity>()

    fun addActivity(activity: Activity){
        activities.add(activity)
    }

    fun removeActivity(activity: Activity){
        activities.remove(activity)
    }

    fun finishAll(){
        for(activity in activities){
            if(!activity.isFinishing){
                activity.finish()
            }
        }
        activities.clear()
    }
}
```

> * 创建完之后，要在Activity实例的onCreate()，和onDestroy()方法来加入和移除列表
> * 有的时候弱项直接点击按钮退出程序，调用```ActivityCollector.finishAll()```即可



## 启动Activity的最佳写法

> Review:我们通过Intent 构建出来当前的“意图”，然后调用startActivity()或者startActivityForResult()方法将Activity启动起来
>
> * 问题：当SecondActivity并不是由你来进行开发的，但你现在负责开发的部分需要启动SecondActivity，二你不清楚启动SecondActivity需要传递那些数据，如何在不进入SecondActivity就解决这个问题呢？

```kotlin
open class BaseActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BaseActivity",javaClass.simpleName)
    }
}
```

**SecondaryActivity继承BaseActivity（）**

```kotlin
class SecondaryActivity : BaseActivity() {

    //写接口
    companion object{
        fun actionStart(context:Context,data1:String){
            val intent=Intent(context,SecondaryActivity::class.java).apply {
                putExtra("extra_data",data1)
            }
//            intent.putExtra("extra_data",data1)
            context.startActivity(intent)
        }
    }
}
```

> 关于companion object会在后面进行讲解

那么，现在我们只需要一行代码就可以启动SecondaryActivity

```kotlin
  button1.setOnClickListener {
            val data="Hello Secondary Activity"
//            val intent=Intent(this,SecondaryActivity::class.java)
//            intent.putExtra("extra_data",data)
//            startActivity(intent)
            SecondaryActivity.actionStart(this,data)
            //显示调用Intent
//            val intent1=Intent(this,SecondaryActivity::class.java)
//            startActivityForResult(intent1,1)
        }
```





# Kotlin的标准函数

> * with
> * run
> * apply

## Lambda表达式初步

> 在讲解标准函数之前，先来回顾一下Lambda表达式

语法：

```kotlin
{p1:type,p2:type->函数体}
//函数体的最后一行代码作为返回值
//若只有一个参数的时候，可以省略不声明参数，直接用it
```

example:

```kotlin
//Lambda表达式测试
val list= mutableListOf<String>("Apple","Banana","Orange")
val map= mapOf("Apple" to 1, "Banana" to 2)

fun main()
{
    for ((fruit,number) in map){
        println("fruit is $fruit, and number is $number")
    }
    list.add("Watermelon")
    val nList=list.filter { it.length<=5 }.map { it.toUpperCase() } //it.length<=5就是函数体
    for(fruit in nList){
        println(fruit)
    }
}
```

### let函数

> ==非空==时候就执行

```kotlin
//let
fun doSomething(fruit: String?){
    fruit?.let { 
        it.toUpperCase()
        it.hashCode()
    }
}
```

## with

> with函数第一个参数就是上下文的对象，并使用lambda表达式中最后一行代码作为返回值返回

```kotlin
    val builder=StringBuilder()
    builder.append("Start eating fruits.\n")
    for (fruit in list){
        builder.append(fruit).append("\n")
    }
    builder.append("Finish Eating.")
    val result=builder.toString()
    println(builder)
-----------------------------下面使用with来实现上面这一段代码--------------------------------
```

```kotlin
val result=with(java.lang.StringBuilder()){
        append("Start Eating.\n") //with的参数就是上下文用到的对象
        for (fruit in list){
            append(fruit).append("\n")
        }
        append("Finish eating")
        toString()//返回值
    }
    println(result)
```



## run

> * with是with(obj){}
> * run是obj.run基本上一模一样 

```kotlin
val result1=java.lang.StringBuilder().run{
        append("Start Eating.\n")
        for (fruit in list){
            append(fruit).append("\n")
        }
        append("Finish eating")
        toString()
    }
    println(result1)
```



## apply

> 返回的就是obj本身！！！

```kotlin
val result=obj.apply{ 
    //定义obj的上下文
} //result=obj,没有指定返回值
```

```kotlin
    val result2=java.lang.StringBuilder().apply{
        append("Start Eating.\n")
        for (fruit in list){
            append(fruit).append("\n")
        }
        append("Finish eating")
        toString()
    }
    println(result2)
```



> Intent之间的传递我们多用apply来实现

```kotlin
 val intent=Intent(context,SecondaryActivity::class.java).apply {
                putExtra("extra_data",data1)
            }
```



# 定义静态方法

> Java在定义静态方法时候非常简单，只需要声明一个static关键字就可以了
>
> * 但在kotlin当中实现静态方法的话就推荐用单例类方式来实现
>   * 不过使用单例类的话就会整个类中方法都是静态的了
>   * 改进一下，我们用companion object

```kotlin
class Until{
    fun doAction1(){
        println("Action1")
    }

    companion object{
        fun doAction2()
        {
            println("Action2")
        }
    }
}
```

其实doAction2()也并不是静态方法，companion object 在Until内部创建了一个伴生类，doAction2()是伴生类当中的实例方法，Java代码去调用的时候不会以静态方式来调用的，想要变成真正的静态方法，加上@JvmStatic注解

> 注意注解只能加在**单例类**或者**companion object**方法当中

```kotlin
 companion object{
        @JvmStatic
        fun doAction2()
        {
            println("Action2")
        }
    }
```

## 顶层方法

> Kotlin会将全部顶层的方法变成静态方法

ex:

顶层方法：

```kotlin
fun doSomething(){
    println("do something now")
}
```

Kotlin当中调用顶层方法非常简单（顶层方法卸载任何位置都可以直接被调用，不管包名和路径）调用直接写

```kotlin
doSomething()//即可
```

在Java当中调用顶层方法就要：

```kotlin
public class DoTest {
    public void testDoSomething(){
        HelperKt.doSomething();
    }
}//Helper为上面那个function的file名字
```

> 因为Java没有顶层的概念，所以要先创建一个类fileNameKt



> 到这里位置Activity就讲解完了，谢谢
>
> 适当休息，继续前进吧！

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
> match_parent表示当前控件的大小和父布局的大小一样
> wrap_content表示让当前控件的大小刚好能够包含住里面的全部内容
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
> * 首先我们定义一个内部类ViewHolder，它继承自RecyclerView.ViewHolder
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

















# 探索Fragment

> 2021.2.18
>
> Gary哥哥的哥哥

> 能够兼顾手机和平板的开发是我们尽可能做到的事情
>
> * Fragment可以让界面在平板上更好的展示

## Fragment是什么

* 嵌入在Activity中的UI片段
* 它能让程序更加合理充分的利用大屏幕空间
* 可以当成是一个mini activity



## Fragment的使用方式

> 首先我们要有一个平板模拟器，这里我选择Pixel C平板模拟器

### 简单用法

> 首先我们屏幕左右各一个Fragment

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#00ff00">
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="center_horizontal"
    android:textSize="24sp"
    android:text="This is the left fragment"
    android:id="@+id/textView1"/>
</LinearLayout>
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="center_horizontal"
    android:text="Button"
    android:id="@+id/button1"
    android:textAllCaps="false"/>
</LinearLayout>
```

**新建左右相关的类都继承Fragment**

> 注意：
>
> * 请使用AndroidX库的Fragment，内置版本的那个已经在android9.0的时候已经被抛弃了
> * 创建新项目的时候勾选 Use androidx.* artifiacts选项即可

> 我们左边为例来说，右边道理是一样的

```kotlin
class LeftFragment:Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.left_fragment,container,false)
    }
}
```

* 重写onCrateView方法，通过这个方法将刚才定义的left_fragment布局**动态加载进来**即可

在activity_main.xml中：

* **手动导入两个Fragment**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

   <fragment
       android:layout_width="0dp"
       android:layout_height="match_parent"
       android:name="com.workaholiclab.useoffragment.LeftFragment"
       android:layout_weight="1"
       android:id="@+id/leftFrag"/>

    <fragment
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:name="com.workaholiclab.useoffragment.RightFragment"
        android:layout_weight="1"
        android:id="@+id/rightFrag"/>

</LinearLayout>
```

> 注意：要有name属性说明是哪个class

``````xml
android:name="com.workaholiclab.useoffragment.LeftFragment"
``````

![img](https://img-blog.csdnimg.cn/20200405105039265.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1NvcGhvcmFl,size_16,color_FFFFFF,t_70)

### 动态添加Fragment

> 上面的方法是静态添加Fragment,其实实际开发中动态添加灵活性更高更好用

我们新写一个left的Fragment xml文件：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#00ff00">
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="center_horizontal"
    android:textSize="24sp"
    android:text="This is another left fragment"/>
</LinearLayout>
```

值得注意的是，这里没有id属性了

```kotlin
class AnotherLeftFragment:Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.another_left_fragment,container,false)
    }
}
```

在activity_main.xml文件当中：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <FrameLayout
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:id="@+id/leftLayout"/>

    <fragment
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:name="com.workaholiclab.useoffragment.RightFragment"
        android:layout_weight="1"
        android:id="@+id/rightFrag"/>

</LinearLayout>
```

```xml
    <FrameLayout
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:id="@+id/leftLayout"/>
```

> **==特别要注意id属性值==**

MainActivity中：

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button1.setOnClickListener {
            replaceFragment(AnotherLeftFragment())
        }
            replaceFragment(LeftFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager=supportFragmentManager
        val transaction=fragmentManager.beginTransaction()
        transaction.replace(R.id.leftLayout,fragment)
        transaction.commit()
    }
}
```



> **==核心代码：==**

```kotlin
private fun replaceFragment(fragment: Fragment) {
        val fragmentManager=supportFragmentManager
        val transaction=fragmentManager.beginTransaction()
        transaction.replace(R.id.leftLayout,fragment)
        transaction.commit()
    }
```

> 来到这里我们需要总结一下动态创建Fragment的流程了
>
> * 前期准备(写好xml文件)
>   * 写好布局文件
>   * 加到activity_main.xml里面就OK了

* 创建待定Fragment的实例（继承Fragment类）
* 获取FragmentManager，在Activity中可以直接调用getSupportFragmentManager方法获取
* 开启一个事物，通过beginTransaction开启
* 想容器内添加或替换Fragment，一般通过replace方法实现
  * 第一个参数:传入容器的id
  * 待定Fragment实例
* 提交事务，调用commit()方法即可



### 在Fragment中实现返回栈

> 按下Back键返回到上一个Fragment

非常简单，只需要加上一行代码即可：

```kotlin
        transaction.addToBackStack(null)
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button1.setOnClickListener {
            replaceFragment(AnotherLeftFragment())
        }
            replaceFragment(LeftFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager=supportFragmentManager
        val transaction=fragmentManager.beginTransaction()
        transaction.replace(R.id.leftLayout,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
```

```        transaction.addToBackStack()```方法可以接受一个用于描述返回栈状态

* 一般传入null即可

### Fragment和Activity之间的交互

> 实际上他们之间的联系确实没有那么紧密，他们都是在不同的类当中的



> 因此为了方便交互，FragmentManager提供了一个类似findViewById的方法
>
> 如下：

```kotlin
        val fragment=supportFragmentManager.findFragmentById(R.id.rightFrag)as RightFragment
```

或者：

> **但更加推荐下面的一种写法**

```kotlin
val fragment=rightFrag as RightFragment
```



> 在每个fragment当中都可以调用和当前Fragment相联系的Activity实例

```kotlin
if(activity!=null){
    val mainActivity=activity as MainActivity
}
```



**同样的，不同的Fragment也间是可以通信的！！！** **==我们使用Activity作为载体即可==**



## Fragment的生命周期

> 这里我就不再文字描述了
>
> 上图片：

![fragmentlife1](E:\kotlin-study\Studying-Kotlin\Fragment\fragmentlife1.jpg)

![fragmentlife2](E:\kotlin-study\Studying-Kotlin\Fragment\fragmentlife2.jpg)



同样Fragment也可以像前面的Activity一样，通过saveInstanceState保存数据

> 前面的例子如下：
>
> ## Activity被回收了怎么办
>
> > 如果A被回收掉了，从B返回A后，仍然可以显示A，但是不会知心onRestart()方法，==而是执行A的onCreate()的方法==，相当于A重新创建了一次
>
> **onSaveInstanceState()回调方法**，在回收之前被调用，对临时数据进行保存：
>
> ```kotlin
>   override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
>         super.onSaveInstanceState(outState, outPersistentState)
>         val tempData="Something you just typed"
>         outState.putString("data_key",tempData)
>     }
>     override fun onCreate(savedInstanceState: Bundle?) {
>         //一般这个Bundle都是空的，现在不是空了！！！
>         super.onCreate(savedInstanceState)
>         setContentView(R.layout.first_layout)
>         if (savedInstanceState!=null){
>             val tempData=savedInstanceState.getString("data_key")
>         }
>     }
> ```
>
> > 可以先将数据保存在Bundle对象中，再将这个对象存放到Intent中。来到目标中再将数据一一去出
> >
> > * 需要注意的是，在横竖屏转化的过程当中，会调用onCreate()的方法，但不推荐用上面的方法来解决，我们后面的章节会降到更好更加优雅的解决方法。







## 动态加载布局的技巧



### 使用限定符

> 在平板上，很多界面都是采取双页模式，像wechat的平板端一样，而手机的屏幕只有一个页面。
>
> 如何让程序对双页单页进行判断呢，这里就要采用限定符了

* 先只保留右侧的fragment

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
    
    <fragment
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:name="com.workaholiclab.useoffragment.RightFragment"
        android:id="@+id/rightFrag"/>

</LinearLayout>
```

* 接着在新建**layout-large**文件夹新建一个两个fragment的布局,同样创建同名的activity_main.xml

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:app="http://schemas.android.com/apk/res-auto"
      xmlns:tools="http://schemas.android.com/tools"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      tools:context=".MainActivity">
  
      <fragment
          android:layout_width="0dp"
          android:layout_weight="1"
          android:layout_height="match_parent"
          android:name="com.workaholiclab.useoffragment.LeftFragment"
          android:id="@+id/leftFrag"/>
      <fragment
          android:layout_width="0dp"
          android:layout_weight="3"
          android:layout_height="match_parent"
          android:name="com.workaholiclab.useoffragment.RightFragment"
          android:id="@+id/rightFrag"/>
  
  </LinearLayout>
  ```

  

> 这样子就大功告成了

下面看看Android的一些常用的限定符：

  ![](E:\kotlin-study\Studying-Kotlin\Fragment\限定符.jpg)



### 使用宽度最小限定符

> 上面的large到底指多大呢？
>
> 有时候我们希望更加灵活地为不同设备加载布局，不管他是不是被认定为large



**这时我们要是用最小宽度限定符**

* 允许我们对屏幕的宽度制定一个最小值，然后以这个最小值为灵界点加载布局

res目录下新建**layout-sw600dp文件夹**，同样创建activity_main.xml

> 意味着我们以600dp为一个最小宽度分界，**==大于==**这个值则会加载下面这个布局

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <fragment
        android:layout_width="0dp"
        android:layout_weight="1"
        android:layout_height="match_parent"
        android:name="com.workaholiclab.useoffragment.LeftFragment"
        android:id="@+id/leftFrag"/>
    <fragment
        android:layout_width="0dp"
        android:layout_weight="3"
        android:layout_height="match_parent"
        android:name="com.workaholiclab.useoffragment.RightFragment"
        android:id="@+id/rightFrag"/>

</LinearLayout>
```



## Fragment最佳实践

> 这里我们以编写一个简易版本的新闻应用为例

* 分隔开的细线是通过View来实现的

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent" android:layout_height="match_parent">
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:id="@+id/contentLayout"
    android:orientation="vertical"
    android:visibility="invisible"
    >
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:padding="10dp"
        android:textSize="20sp"
        android:id="@+id/newsTitle"
        />
    <View
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:background="#000"
        />

    <TextView
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:padding="15dp"
        android:textSize="18sp"
        android:id="@+id/newsContent"/>
</LinearLayout>
    <View
        android:layout_width="1dp"
        android:layout_height="match_parent"
        android:layout_alignParentLeft="true"
        android:background="#000"
</RelativeLayout>
```

> 我们设定成invisible表示单页模式下不点开来是不会显示出来的

```kotlin
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
```

```kotlin
class NewsContentFragment:Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inflater.inflate(R.layout.news_content_frag,container,false)
    }
    
    fun refresh(title:String,content:String)
    {
        contentLayout.visibility=View.VISIBLE
        //刷新内容
        newsContent.text=content
        newsTitle.text=title
    }
}
```

**如果想在单页模式下使用：**

* activity_news_content.xml（新建）

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">
<fragment
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:id="@+id/newsContentFrag"
    android:name="com.workaholiclab.bestparcticefragment.NewsContentFragment"/>
</LinearLayout>
```

这里我们充分发挥了代码的复用性，直接在布局当中引入NewsContentFragment



> 接下开我们还需要一个显示新闻列表的布局

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">
<androidx.recyclerview.widget.RecyclerView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:id="@+id/newsTitleRecyclerView"/>
</LinearLayout>
```

它的子布局：

```android:ellipsize="end"```用于设定当前文本内容超出控件宽度时候文本的缩略方式，这里指尾部缩略

```xml
<?xml version="1.0" encoding="utf-8"?>
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/newsTitle" android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:maxLines="1"
    android:ellipsize="end"
    android:textSize="18sp"
    android:paddingLeft="10dp"
    android:paddingRight="10dp"
    android:paddingBottom="10dp"
    android:paddingTop="10dp"
    />

```

新建Fragment:

```kotlin
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
    }
}
```

* 这里我们通过onActivityCreted方法在Activity中能否找到对饮id的View，来判断当前是单页还是双页

activity_main.xml：

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/newsTitleLayout"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >

<fragment
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:name="com.workaholiclab.bestparcticefragment.NewsTitleFragment"
    android:id="@+id/newsTitleFrag"/>

</FrameLayout>
```

同样新建一个sw600dp的activity_main.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="horizontal" android:layout_width="match_parent"
    android:layout_height="match_parent">
<fragment
    android:layout_width="0dp"
    android:layout_height="match_parent"
    android:layout_weight="1"
    android:name="com.workaholiclab.bestparcticefragment.NewsTitleFragment"
    android:id="@+id/newsTitleFrag"/>
    <FrameLayout
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_weight="3"
        android:id="@+id/newsContentLayout">
        <fragment
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:id="@+id/newsContentFrag"
            android:name="com.workaholiclab.bestparcticefragment.NewsContentFragment"/>
    </FrameLayout>
</LinearLayout>
```

> 注意：id是可以一样的

> 在往下做的过程中建议想清楚上面的步骤先

* 接下来我们就是做Adapter了

  > **==这理我们在NewsTitleFragment中新建一个内部内Adapter来适配==**

  ```kotlin
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
  ```

  > 这里写成内部类的好处是可以直接访问NewsTitleFragment中的变量，如isTwoPane

* 收尾工作，像RecyclerView中填充数据

```kotlin
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
```



> Fragment博客完工



# 全局大喇叭，广播机制

> 2021.2.21
>
> Gary哥哥的哥哥的哥哥

> 在一个IP网络范围中，最大的IP地址是被保留作为广播地址来使用的
>
> * 为了便于进行系统级别的消息通知，Android也引入了一套类似的广播消息机制。

## 广播机制简介

> Android的广播机制相比其他而言，**更加的灵活**。
>
> 无论这些广播是来自系统的还是来自其他应用程序的，Android都提供了一套完整的API，允许应用程序发送和接受广播

* 标准广播（normal broadcasts）是一种完全异步的广播，发出后，所有的接收方几乎在同一时间里面接收到。
  * 效率高
  * 无法被拦截
* 有序广播（ordered broadcasts）是一种同步的广播，优先级高的广播先接收到，有序的



## 接受系统广播

> BroadcastReceiver的具体用法

### 动态注册监听时间变化

> 静态注册：在AndroidManifest.xml文件注册
>
> 动态注册：在代码中注册

```kotlin
class MainActivity : AppCompatActivity() {
    lateinit var timeChangeReceiver: TimeChangeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intentFilter=IntentFilter()
        intentFilter.addAction("android.intent.action.TIME_TICK")
        timeChangeReceiver=TimeChangeReceiver()
        registerReceiver(timeChangeReceiver,intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(timeChangeReceiver)
    }

    inner class TimeChangeReceiver:BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(context,"Time has changed",Toast.LENGTH_SHORT).show()
        }
    }
}
```



* 我们写了一个内部类继承BroadcasiReceiver```inner class TimeChangeReceiver:BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
          Toast.makeText(context,"Time has changed",Toast.LENGTH_SHORT).show()
      }
  }```
* 观察onCreate方法，添加一个action进去android.intent.action.TIME_TICK表示过一分钟的action
* 动态注册：```registerReceiver(timeChangeReceiver,intentFilter)```参数分别为接受器，和发送action
* 动态注册完记得onDestroy()方法中取消注册```unregisterReceiver(timeChangeReceiver)```

> **==如果你想看完整的系统广播列表,到如下的路径去观看：==**（说实话，上网查更好）
>
> * <Android SDK>/platforms/<任意 android api 版本>/data/broadcast_actions.txt

### 静态注册开机启动

> 动态注册确实在灵活性方面优势比较大，但它最大的缺点就是需要程序启动才能接受广播，逻辑要写在onCreate()方法中
>
> * 若是想不执行程序接受广播，这就需要使用静态注册的方法了

> 但由于一些原因，Android的版本每次都在削减静态注册的功能，这些原因android的用户都深有体会

* 在这些特殊的广播机制中，有一条为android.intent.action.BOOT_COMPLETED的，是一条开机的广播，我们使用它来举例

> ==我们通过该包下New->Other->BroadcastReceiver来创建线面的类==

```kotlin
class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Toast.makeText(context,"Boot Complete",Toast.LENGTH_SHORT).show()
    }
}
```

* 可看看AndroidManifest.xml多了什么



> 接下来我们还需要对AndroidManifest.xml进行修改，才可以接收这个广播

```xml
 <receiver
            android:name=".BootCompleteReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED"/>
            </intent-filter>
        </receiver>
```

> 加上上面这个才行，如果通过我上面的方式来创建的话，只需要加上：

```xml
<intent-filter>
	<action android:name="android.intent.action.BOOT_COMPLETED"/>
</intent-filter>
```



**特别提醒，==不要再onReceive方法中添加非常复杂的逻辑代码，或者耗时的操作，==因为BroadcastReceiver是不允许开启线程的，长时间不结束onReceive就会程序崩溃**



### 发送自定义广播

> 我们发送一个MyBroadcast

* 操作和上面很类似：

```kotlin
class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Toast.makeText(context,"received in MyBroadcastReceiver",Toast.LENGTH_SHORT).show()
    }
}
```

```xml
<receiver
            android:name=".MyBroadcastReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="com.workaholiclab.receivertest.MY_BROADCAST"/>
            </intent-filter>
        </receiver>
```

MainActivity中测试:

> * 首先把要发送的广播值放入intent对象里面
> * setPackage(packageName)传入当前应用程序的包名
> * 最后发送出去

```kotlin
 bt1.setOnClickListener { 
            val intent=Intent("com.workaholiclab.receivertest.MY_BROADCAST")
            intent.setPackage(packageName)
            sendBroadcast(intent)
        }
```

> 其实这些广播都是intent发出去的，因此你可以在intent中携带一些数据传递给相应的接收器，和Activity用法很类似

**我们知道，Intent  是一个消息传递对象，使用它可以向其他Android组件请求操作。Intent的基本用途主要包括：启动  Activity、启动服务、传递广播。Intent分为显式Intent和隐式Intent。下面我通过启动Activity来讲解学习Intent。**

==Intent对象可以封装传递下面6种信息：==

1. 组件名称（ComponentName）
2. 动作（Action）
3. 种类（Category）
4. 数据（Data）
5. 附件信息（Extra）
6. 标志（Flag）



[相关Intent知识]: https://blog.csdn.net/salary/article/details/82865454	"相关Intent知识"



### 发送有序广播

> 我们使用AnotherBroadcastReceiver来演示一下
>
> * 操作和上面的步骤类似

```kotlin
class AnotherBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Toast.makeText(context,"received in AnotherBroadcastReceiver",Toast.LENGTH_SHORT).show()
    }
}
```

```xml
<receiver
            android:name=".AnotherBroadcastReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="com.workaholiclab.receivertest.MY_BROADCAST"/>
            </intent-filter>
        </receiver>
```



```kotlin
    bt1.setOnClickListener {
            val intent=Intent("com.workaholiclab.receivertest.MY_BROADCAST")
            intent.setPackage(packageName)
            sendOrderedBroadcast(intent,null)
        }
```

==**你会发现细小的区别就是**==```sendOrderedBroadcast(intent,null)```

* 第一个参数仍然是Intent
* 第二个参数是一个与权限有关的字符串

**此时两个name一样的广播都会收到，但他们区别就是一个是标准，一个有序**



> 如何规定BroadReceiver的先后顺序，当然是在注册文件当中去修改

```xml
<receiver
    android:name=".MyBroadcastReceiver"
    android:enabled="true"
    android:exported="true">
    <intent-filter android:priority="100">
        <action android:name="com.workaholiclab.receivertest.MY_BROADCAST" />
    </intent-filter>
</receiver>
```

> 把priority值设置为100，意味着先接收到,MyBroadcast还能有决定是否再允许广播传递



```kotlin
class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Toast.makeText(context,"received in MyBroadcastReceiver",Toast.LENGTH_SHORT).show()
        abortBroadcast()
    }
}
```

```abortBroadcast()```**意味着广播截断**







## 广播功能最佳实践：实现强制下线功能                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               

> 这个在生活中中的例子很多，像QQ等应用都会有强制下线的功能

> 这个实现比较简单，不再过多阐述，详细代码见我github仓库中的ForceOffline文件



> 广播机制完更





# 数据存储全方案

> 2021.2.22
>
> Gary哥哥的哥哥的哥哥

> 详解持久化操作

## 持久化技术简介

数据持久化就是指那些内存中的瞬时数据保存到存储设备上，保证即使设备开机关机，这些数据仍然不会丢失

Android主要提供了三种方式用于简单实现数据持久化功能：

* 文件存储
* SharedPreference存储
* 数据库存储

> 下面对上面的三种方式一一展开讲解

## 文件存储

### 写入数据

> Android中最基本的数据存储方式，他不对存储内容进行任何格式化的处理
>
> * 比较适合存储一些简单的文本数据或二进制数据
> * 如果你想保存一些较为复杂的结构化数据，就需要定义一套自己的格式规范了，方便之后数据从文件中重新解析出来

> Context类中提供一个openFileOutput()方法，可以用于将数据存储到指定的文件中，两个参数
>
> * p1为文件名
>   * **文件创建时候使用，注意，不带路径的，有一个默认存储的位置，/data/data/com.workaholiclab.savefile/files/data**
>     * **这里要打开Android studio右下角的Device File Explorer这个工具（如果没找到就ctrl+Shift+A 输入进去）**
> * p2为文件的操作模式
>   * MODE_PRIVATE，覆盖原文件内容
>   * MODE_APPEND，加载源文件后面
>   * （默认是第一个）
> * 返回的是一个FileOutputStream对象，得到这个对象后，可以使用Java流的方式将数据写入文件中
> * **==注意两种模式，如果文件不存在的话，都会自动创建！！！==**
>
> 下面是简单的代码示例：

```kotlin
    fun save(inputText:String){
        try {
            val output=openFileOutput("data", Context.MODE_PRIVATE)
            val writer=BufferedWriter(OutputStreamWriter(output))
            writer.use { 
                it.write(inputText)
            }
        }catch (e:IOException){
            e.printStackTrace()
        }
    }
```

> 前面的操作和Java很类似
>
> 这里还用到了一个use函数，是一个Kotlin提供的一个内置扩展函数
>
> **它会保证Lambda表达式中的代码全部执行完之后自动将外层的流关闭，这样就不惜要手动写一个finnally语句手动关闭流**

下面修改一下activity_main.xml的代码：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

<EditText
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Type something here"
    android:id="@+id/editText"/>

</LinearLayout>
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onDestroy() {
        super.onDestroy()
        val inputText=editText.text.toString()
        save(inputText)
    }

    fun save(inputText:String){
        try {
            val output=openFileOutput("data", Context.MODE_PRIVATE)
            val writer=BufferedWriter(OutputStreamWriter(output))
            writer.use {
                it.write(inputText)
            }
        }catch (e:IOException){
            e.printStackTrace()
        }
    }
}
```



### 从文件中读取数据

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val inputText=load()
        if(inputText.isNotEmpty()){
            editText.setText(inputText)
            editText.setSelection(inputText.length)
            Toast.makeText(this,"Restoring succeeded",Toast.LENGTH_SHORT).show()
        }
    }
    private fun load():String{
        val content=StringBuilder()
        try {
            val input=openFileInput("data")
            val reader=BufferedReader(InputStreamReader(input))
            reader.use {
                reader.forEachLine {
                    content.append(it)
                }
            }
        }catch (e:IOException){
            e.printStackTrace()
        }
        return content.toString()
    }
}
```

注意下面这条语句：

```kotlin
editText.setSelection(inputText.length)
```

==**将输入光标移动到文本的末尾位置便于继续输入**==



## SharedPreferences 存储

### 保存数据

> * 是一种键值对的方式来存储数据
> * 支持多种不同的数据结构
> * 存储数据和读取的数据是同一类型
> * 数据持久化比使用文件方便很多

> 想要存储数据，就必须先获得SharedPreferences对象，下面提供两种方法获得对象

* Context类中的getSharedPreferences方法
  * p1文件的名称，指定目录在：/data/data/com.workaholiclab.savefile/shared_prefs目录下
  * p2指定模式，其实目前只有一种模式：MODE_PRIVATE，他和直接传入0的效果是一致的
    * 表示只有当前应用程序可以对这个SharedPreferences文件进行读写
* Activity类中的getSharedPreferences方法
  * 只接受一个操作模式的参数
  * 自动将类名作为文件名



> 得到对象过后，开始想SharedPreferences文件进行存储数据，主要分三步实现：

* 调用SharedPreferences对象的edit方法获取SharedPreferences.Editor对象
* 向上面获得的对象添加数据，比如一个整型的数据就使用putInt()
* 调用apply的方法将添加的数据提交



> 下面用代码演示一下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <Button
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:text="Save Data"
        android:textAllCaps="false"
        android:id="@+id/saveButton"/>

</LinearLayout>
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        saveButton.setOnClickListener { 
            val editor=getSharedPreferences("data", Context.MODE_PRIVATE).edit()
            editor.putString("name","Wendy")
            editor.putInt("age",20)
            editor.putBoolean("married",false)
            editor.apply()
        }
    }
}
```

> 看到数据文件是以xml方式保存的

```xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <string name="name">Wendy</string>
    <boolean name="married" value="false" />
    <int name="age" value="20" />
</map>

```

### 读取数据

> 操作很类似

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Save Data"
        android:textAllCaps="false"
        android:id="@+id/saveButton"/>
    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Restore Data"
        android:id="@+id/restoreButton"/>

</LinearLayout>
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        saveButton.setOnClickListener {
            val editor=getSharedPreferences("data", Context.MODE_PRIVATE).edit()
            editor.putString("name","Wendy")
            editor.putInt("age",20)
            editor.putBoolean("married",false)
            editor.apply()
        }
        restoreButton.setOnClickListener {
            val prefs=getSharedPreferences("data",Context.MODE_PRIVATE)
            val name=prefs.getString("name","")
            val age=prefs.getInt("age",0)
            val married=prefs.getBoolean("married",false)
            println("$name $age $married")
        }
    }
}
```

> get***的第二个参数数默认值，找不到对应的key就当成默认值



### 记住密码功能实现

> 利用SharedPreferences存储可以实现简单的记住密码的功能:
>
> * 下面通过上一章节的登录页面修改来实现功能：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".LoginActivity"
    android:orientation="vertical">
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="60dp"
        android:orientation="horizontal">
        <TextView
            android:layout_width="90dp"
            android:layout_height="wrap_content"
            android:layout_gravity="center_vertical"
            android:textSize="18sp"
            android:text="Account:"/>
        <EditText
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:layout_gravity="center_vertical"
            android:id="@+id/accountEdit"/>
    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="60dp"
        android:orientation="horizontal">
        <TextView
            android:layout_width="90dp"
            android:layout_height="wrap_content"
            android:layout_gravity="center_vertical"
            android:textSize="18sp"
            android:text="Password:"/>
        <EditText
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:layout_gravity="center_vertical"
            android:id="@+id/passwordEdit"
            android:inputType="textPassword"/>
    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">
        <CheckBox
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:id="@+id/rememberPass"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="18sp"
            android:text="Login"
            android:textAllCaps="false"/>
    </LinearLayout>
    <Button
        android:layout_width="200dp"
        android:layout_height="60dp"
        android:layout_gravity="center_horizontal"
        android:text="Login"
        android:id="@+id/login"
        android:textAllCaps="false"/>

</LinearLayout>

```





```kotlin
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val prefs=getPreferences(Context.MODE_PRIVATE)
        val isRemember=prefs.getBoolean("remember_password",false)
        if(isRemember){
            //账号密码都放到文本框上
            val account=prefs.getString("account","")
            val password=prefs.getString("password","")
            accountEdit.setText(account)
            passwordEdit.setText(password)
            rememberPass.isChecked=true
        }
        login.setOnClickListener {
            val account=accountEdit.text.toString()
            val password=passwordEdit.text.toString()
            if(account=="admin"&&password=="123456")
            {
                val editor=prefs.edit()
                if(rememberPass.isChecked){
                    editor.putBoolean("remember_password",true)
                    editor.putString("account",account)
                    editor.putString("password",password)
                }else{
                    editor.clear()
                }
                editor.apply()
                val intent= Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this,"account or password is invalid",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
```





## SQLite 数据库存储

> Android是内置了数据库的
>
> SQLite是一款轻量级的关系型数据库，迅速按书读非常快，占用资源很少
>
> * 不仅支持SQL语法
> * 遵循数据库的ACID事物
>
> 应用：
>
> * 存储大量关系复杂的数据型数据





**==这里以后有空再把全部的示例讲解代码补上，现在先看看书吧==**

**一系列的增删改查（CRUD）操作**

> 我这里知识简单演示一下代码
>
> * ==**具体的函数用法见书本p288开始**==

```kotlin
class MyDatabaseHelper(val context: Context,name:String,version:Int):SQLiteOpenHelper(context,name,null,version) {
    private val createBook="create table Book ("+
            "id integer primary key autoincrement,"+
            "author text,"+
            "price real,"+
            "pages integer,"+
            "name text)"
    private val createCategory="create table Category ("+
            "id integer primary key autoincrement,"+
            "category_name text,"+
            "category_code integer)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createBook)
        db?.execSQL(createCategory)
        Toast.makeText(context,"Create succeeded",Toast.LENGTH_SHORT).show()
    }

    //升级数据库,比如像加多一张表
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists Book")
        db?.execSQL("drop table if exists Category")
        onCreate(db)
    }
}
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dbHelper=MyDatabaseHelper(this,"BookStore.db",1)
        createDatabase.setOnClickListener {
            dbHelper.writableDatabase
        }
        //添加数据
        addData.setOnClickListener {
            val db=dbHelper.writableDatabase
            val values1=ContentValues().apply {
                //开始组装第一条数据
                put("name","The Da Vinci Code")
                put("author","Dan Brown")
                put("pages",454)
                put("price",19.95)
            }
            db.insert("Book",null,values1)//插入第一条数据
            val values2=ContentValues().apply {
                //开始组装第一条数据
                put("name","First Code")
                put("author","guolin")
                put("pages",692)
                put("price",99.00)
            }
            db.insert("Book",null,values2)//插入第二条数据

            Toast.makeText(this,"成功添加数据",Toast.LENGTH_SHORT).show()
        }

        //更新数据
        updateData.setOnClickListener {
            val db=dbHelper.writableDatabase
            val values=ContentValues()
            values.put("price",10.99)
            db.update("Book",values,"name = ?", arrayOf("The Da Vinvi Code"))
            Toast.makeText(this,"更新数据成功",Toast.LENGTH_SHORT).show()
        }

        //删除数据
        deleteData.setOnClickListener {
            val db=dbHelper.writableDatabase
            db.delete("Book","pages > ?", arrayOf("500"))
            Toast.makeText(this,"删除数据成功",Toast.LENGTH_SHORT).show()
        }

        //查询数据
        queryData.setOnClickListener {
            val db=dbHelper.writableDatabase
            //查询Book表中所有的标准数据
            val cursor=db.query("Book",null,null,null,null,null,null)
            if(cursor.moveToFirst()){
                do {
                    //遍历Cursor对象,取出数据并打印
                    val name=cursor.getString(cursor.getColumnIndex("name"))
                    val author=cursor.getString(cursor.getColumnIndex("author"))
                    val pages=cursor.getInt(cursor.getColumnIndex("pages"))
                    val price=cursor.getDouble(cursor.getColumnIndex("price"))
                    println("$name $author $pages $price")
                }while (cursor.moveToNext())
            }
            cursor.close()
            Toast.makeText(this,"查询数据成功",Toast.LENGTH_SHORT).show()
        }

        //使用事务
        replaceData.setOnClickListener {
            val db=dbHelper.writableDatabase
            db.beginTransaction()//开启事务
            try {
                db.delete("Book",null,null)
                if(true){
                    //手动抛出一个异常让事务失败
                    throw NullPointerException()
                }
                val values=ContentValues().apply {
                    put("name","Game of Thrones")
                    put("author","George Martin")
                    put("pages",720)
                    put("price",20.85)
                }
                db.insert("Book",null,values)
                db.setTransactionSuccessful()//事务已经执行成功
            }catch (e:IOException){
                e.printStackTrace()
            }finally {
                db.endTransaction()//结束事务
            }
        }
    }
}
```





## SQLite的最佳实现

> SQLite数据库是支持事务的，事务的特性可以保证让一些列操作要么全部完成，要么一个都不会完成。
>
> * 如，银行转账双方

```kotlin
 //使用事务
        replaceData.setOnClickListener { 
            val db=dbHelper.writableDatabase
            db.beginTransaction()//开启事务
            try {
                db.delete("Book",null,null)
                if(true){
                    //手动抛出一个异常让事务失败
                    throw NullPointerException()
                }
                val values=ContentValues().apply { 
                    put("name","Game of Thrones")
                    put("author","George Martin")
                    put("pages",720)
                    put("price",20.85)
                }
                db.insert("Book",null,values)
                db.setTransactionSuccessful()//事务已经执行成功
            }catch (e:IOException){
                e.printStackTrace()
            }finally {
                db.endTransaction()//结束事务
            }
        }
```





## 升级数据库的最佳写法

> 在升级数据库中，在onUpgrade方法调用onCreate方法是非常粗暴的，在开发时候可以使用，但产品上线之后千万不要使用

```kotlin
class MyDatabaseHelper(val context: Context,name:String,version:Int):SQLiteOpenHelper(context,name,null,version) {
    private val createBook="create table Book ("+
            "id integer primary key autoincrement,"+
            "author text,"+
            "price real,"+
            "pages integer,"+
            "name text,"+
            "category_id integer)"
    private val createCategory="create table Category ("+
            "id integer primary key autoincrement,"+
            "category_name text,"+
            "category_code integer)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createBook)
        db?.execSQL(createCategory)
        Toast.makeText(context,"Create succeeded",Toast.LENGTH_SHORT).show()
    }

    //升级数据库,比如像加多一张表
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if(oldVersion<=1)
            db?.execSQL(createCategory)
        if (oldVersion<=2){
            db?.execSQL("alter table Book add column category_id integer")
        }
    }
    
}
```

> MainActivity中版本记得也修改为3哦

加多一个属性：

```kotlin
db?.execSQL("alter table Book add column category_id integer")
```





> 数据库博客完更



# 探索 ContentProvider

> 2021.2.23
>
> Gary哥哥的哥哥的哥哥

> **跨程序共享数据**
>
> 我们前面学到的持久化技术所保存的数据都只能在当前应用程序中访问
>
> 虽然SharedPreferences存储中提供了其他模式，但在早期的Android版本已经将其废弃，安全性也很差
>
> 下面我们推荐使用更加安全可靠的ContentProvider技术



## 简介

> 用于在不同的应用程序之间实现数据共享的功能，它提供一套完整的机制，同时确保访问数据的安全性
>
> 在正式学习ContentProvider前，我们需要先掌握另外一个非常重要的知识-----**Android的运行权限**

## 运行权限

> ==**这部分知识点文字比较多，详见书本p319页**==

![](E:\kotlin-study\Studying-Kotlin\ContentProvider\危险权限.jpg)





### 在运行时申请权限

> 为了简单起见，我们就使用CALL_PHONE这个权限来作为示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

   <Button
       android:layout_width="match_parent"
       android:layout_height="wrap_content"
       android:text="Make Call"
       android:id="@+id/makeCall"/>
</LinearLayout>
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        makeCall.setOnClickListener {
            try{
                val intent=Intent(Intent.ACTION_CALL)
                intent.data=Uri.parse("tel:10086")
                startActivity(intent)
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }
}
```

> 在注册文件中:

```XML
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.workaholiclab.runtimepermissiontest">
    <uses-permission android:name="android.permission.CALL_PHONE"/>

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

加了一句```<uses-permission android:name="android.permission.CALL_PHONE"/>```



* ==这样子是无法成功地，由于权限被禁止所导致==
* Android6.0开始，**系统在使用危险权限时必须进行运行时权限处理**

> 修改MainActivity的代码：

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        makeCall.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.CALL_PHONE),1)
            }else{
                call()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1->{
                if (grantResults.isNotEmpty()&&
                        grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    call()
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun call() {
        try {
            val intent=Intent(Intent.ACTION_CALL)
            intent.data=Uri.parse("tel:10086")
            startActivity(intent)
        }catch (e:IOException){
            e.printStackTrace()
        }
    }
}
```

* 上面这个代码看似有点复杂，让我们慢慢讲解

> 具体流程如下：

1. 判断用户是不是已经给过我们权限了，借助ContextCompat.checkSelfPermission()方法

```kotlin
ContextCompat.checkSelfPermission(this,android.Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED
```

* p1:Context
* p2：具体的权限名
* 这里与PackageManager.PERMISSION_GRANTED做对比，相等说明已经授权

2. 拨打电话的逻辑封装到call方法当中，**如果没有授权则需要调用ActivityCompat.requestPermissions()**方法来申请权限

``````
 ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.CALL_PHONE),1)
``````

requestPermissions: 

* p1:Activity实例
* p2：String数组，申请的权限名
* p3：唯一值即可，这里传入1

3. 权限申请对话窗口

   * 不管你按哪个选项，都会调用onRequestPermissionsResult方法
   * 授权的结果放在grantResults
   * 判断一下授权的结果即可：

   ```kotlin
   override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
           super.onRequestPermissionsResult(requestCode, permissions, grantResults)
           when(requestCode){
               1->{
                   if (grantResults.isNotEmpty()&&
                           grantResults[0]==PackageManager.PERMISSION_GRANTED){
                       call()
                   }else{
                       Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show()
                   }
               }
           }
       }
   ```

> 下面进入ContentProvider





## 访问其他程序中的数据

> ContentProvider的用法一般有两种
>
> * 使用现有的ContentProvider读取和操作相应程序中的数据
> * 创建自己的ContentProvider对其数据提供外部访问的接口，方便其他应用程序访问

### ContentProvider的基本用法

> 借助ContentResolver类，**Context中的getContentResolver()方法可以获取该类的实例！！！**
>
> * 其提供了一些列的对数据的增删改查操作，与前面SQLite的方法类似，只不过在参数上有稍微的一些区别

ContentResolver中的增删改查方法都是**不接收表名参数的**，而是**使用一个Uri参数代替**

URI主要由authority和path构成：

* authority是不同应用程序作区分的，为了避免冲突，会采用包名的方式进行命名 包名.provider
* path则是同一应用程序里面对不同的table做区分命名为/table1
* ∴结合起来就是ex: com.example.app.provider/table1

**但标准URI的格式如下：**

content://com.example.app.provider/table1

content://com.example.app.provider/table2



解析成URI对象的方法也很简单

```kotlin
val uri=Uri.parse("content://com.example.app.provider/table1")
```

> 对table1表中数据进行查询如下

```kotlin
val cursor=contentResolver.query(uri,projection,selection,selectionArgs,sortOrder)
```

查询完成后返回的依然是一个cursor对象，然后取出每一行中相应列的数据，如下：

```kotlin
while(cursor.moveToNext()){
    val column1= cursor.getString(cursor.getColumnIndex("column1"))
    val column2= cursor.getString(cursor.getColumnIndex("column2"))
}
cursor.close()
```

> **==其他CRUD操作具体见书本p328==**





### 实践：读取系统联系人

> 由于我们在模拟器中操作，我们需要在模拟器中手动加两个联系人先

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <ListView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/contactsView"/>

</LinearLayout>
```

> 为了让代码少一点，好看一点，我们使用ListView来做示范，当然RecyclerView也是完全可以的

> **==记得注册文件加上==**

```xml
<uses-permission android:name="android.permission.READ_CONTACTS"/>
```



```kotlin
class MainActivity : AppCompatActivity() {

    private val contactsList=ArrayList<String>()
    private lateinit var adapter:ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,contactsList)
        contactsView.adapter=adapter
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CONTACTS),1)
        }else{
            readContacts()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1->{
                if (grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    readContacts()
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun readContacts() {
        //查询联系人
        contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)?.apply {
            while (moveToNext()) {
                //获取联系人
                val displayName =
                    getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number =
                    getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactsList.add("$displayName\n$number")
            }
            adapter.notifyDataSetChanged()
            close()
        }
    }
}
```

> 这里我们没有用到Uri.prase()了，因为这个权限的用法本来就Android帮我们封装好了



## 创建自己的ContentProvider

> 继承ContentProvider的时候，需要重写6个抽象方法

```kotlin
class MyContentProvider: ContentProvider() {
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        TODO("Not yet implemented")
    }

    override fun onCreate(): Boolean {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }
}
```

> 这几个方法和之前SQLite的很类似，这里就不再展开来一一讲解了
>
> **==详见书本p333页==**
>
> * getType()根据传入的内容URI返回相应的MIME类型

很多方法里带有uri这个参数，正好这个参数是用来调用ContentResolver的CRUD操作的。

现在们需要对传入的uri参数进行解析，从中分析出调用方期望访问的表和数据

content://com.example.app.provider/table1/1

> 除了上面说过的不加id的写法，后面加多一个id为第二种写法，id为1的数据



通配符分别匹配两种格式的URI

* *表示匹配任意长度的任意字符
* #表示匹配任意长度的数字

> example:

* ==匹配任意表的内容==

content://com.example.app.provider/*

* ==匹配table1表中任意一行数据的内容URI==

content://com.example.app.provider/table1/#



> **接着，我们借助UriMatcher这个类轻松实现匹配内容URI的功能**



* 有一个addURI（）方法,可以分别发authority，path和一个自定义代码传进去。这样当调用UriMatcher的match()方法是，就可以将一个Uri对象春如，返回值是某个能够匹配这个Uri对象所对应的自定义代码，利用这个代码，我们就可以判断出调用放期望访问的是哪张表中的数据，我们还是搞点示例代码更加清楚

```kotlin
class MyContentProvider: ContentProvider() {
    private val table1Dir=0
    private val table1Item=1
    private val table2Dir=2
    private val table2Item=3
    
    private val uriMatcher=UriMatcher(UriMatcher.NO_MATCH)
    
    init {
        uriMatcher.addURI("com.workaholiclab.app.provider","table1",table1Dir)
        uriMatcher.addURI("com.workaholiclab.app.provider","table1/#",table1Item)
        uriMatcher.addURI("com.workaholiclab.app.provider","table2",table2Dir)
        uriMatcher.addURI("com.workaholiclab.app.provider","table2/#",table2Item)
    }
    
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        when(uriMatcher.match(uri)){
            table1Dir->{
                //查询table1表的所有数据
            }
            table1Item->{
                //查询table1表中的单条数据
            }
        table2Dir->{
                //查询table2表的所有数据
            }
            table2Item->{
                //查询table2表中的单条数据
            }
        }
    }

    override fun onCreate(): Boolean {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }
}
```

**getType()方法：**

用于获取Uri对象所对应的MINE类型。

> 一个内容URI的MIME字符串主要由三部分组成，Android对这三个部分做了如下格式规定：

* 必须以vnd开头
* 如果内容URI以路径结尾，则后接android.cursor.dir/;如果内容URI以id结尾，则后面接上android.cursor.item/。
* 最后接上 vnd.<authority>.<path>。

> example:

* 对于上面content://com.example.app.provider/table1 这个内容URI，它所对应的MIME类型就可以写成：

```vnd.android.cursor.dir/vnd.com.example.app.provider.table1```

* 对于content://com.example.app.provider/table1/1则可以写成

```vnd.android.cursor.item/vnd.com.example.app.provider.table1```

下面我们对getType()方法中的逻辑继续完善，代码如下所示：

```kotlin
class MyContentProvider: ContentProvider() {
    private val table1Dir=0
    private val table1Item=1
    private val table2Dir=2
    private val table2Item=3

    private val uriMatcher=UriMatcher(UriMatcher.NO_MATCH)

    init {
        uriMatcher.addURI("com.workaholiclab.app.provider","table1",table1Dir)
        uriMatcher.addURI("com.workaholiclab.app.provider","table1/#",table1Item)
        uriMatcher.addURI("com.workaholiclab.app.provider","table2",table2Dir)
        uriMatcher.addURI("com.workaholiclab.app.provider","table2/#",table2Item)
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        when(uriMatcher.match(uri)){
            table1Dir->{
                //查询table1表的所有数据
            }
            table1Item->{
                //查询table1表中的单条数据
            }
        table2Dir->{
                //查询table2表的所有数据
            }
            table2Item->{
                //查询table2表中的单条数据
            }
        }
    }

    override fun onCreate(): Boolean {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun getType(uri: Uri)=when(uriMatcher.match(uri)) {
        table1Dir->"vnd.android.cursor.dir/vnd.com.workaholic.app.provider.table1"
        table1Item->"vnd.android.cursor.item/vnd.com.workaholic.app.provider.table1"
        table2Dir->"vnd.android.cursor.dir/vnd.com.workaholic.app.provider.table1"
        table2Item->"vnd.android.cursor.item/vnd.com.workaholic.app.provider.table2"
        else -> null
    }
}
```

> 到这里就搞定了，任意一个应用程序都可以使用ContentResolver访问我们应用程序的数据
>
> * 为了保证隐私数据不要泄露出去，这里也不知不觉的解决了这个问题，因为我们不可能像UriMatcher中添加隐私数据的URI，所以这部分数据根本无法被外部程序访问，安全问题也就不存在了

> 下面来尝试实战一下吧

## 实战：实现跨程序数据共享

> 我们还是在上一章节中的DatabaseTest项目的基础上继续开发

> 在包名下New-> Other ->Content Provider

==**下面这个代码确实比较长，我们后续会慢慢讲解的！！！**==

> **==若讲解得不够细致，也可以看书本p339==**

```kotlin
class DatabaseProvider : ContentProvider() {
    private val bookDir=0
    private val bookItem=1
    private val categoryDir=2
    private val categoryItem=3
    private val authority="com.workaholiclab.databasetest.provider"
    private var dbHelper:MyDatabaseHelper?=null

    private val uriMatcher by lazy {
        val matcher=UriMatcher(UriMatcher.NO_MATCH)
        matcher.addURI(authority,"book",bookDir)
        matcher.addURI(authority,"book/#",bookItem)
        matcher.addURI(authority,"category",categoryDir)
        matcher.addURI(authority,"category/#",categoryItem)
        matcher
    }


    //创建
    override fun onCreate()=context?.let {
        dbHelper=MyDatabaseHelper(it,"BookStore.db",2)
        true
    }?:false

    //查询数据
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    )=dbHelper?.let {
        val db = it.readableDatabase
        val cursor=when(uriMatcher.match(uri)){
            bookDir->db.query("Book",projection,selection,selectionArgs,null,null,sortOrder)
            bookItem->{
                val bookId=uri.pathSegments[1]
                db.query("Book",projection,"id = ?", arrayOf(bookId),null, null,sortOrder)
            }
            categoryDir->db.query("Category",projection,selection,selectionArgs,null,null,sortOrder)
            categoryItem->{
                val categoryId=uri.pathSegments[1]
                db.query("Category",projection,"id = ?", arrayOf(categoryId),null,null,sortOrder)
            }
            else->null
        }
        cursor
    }

    //插入（添加）数据
    override fun insert(uri: Uri, values: ContentValues?)=dbHelper?.let {
        val db=it.writableDatabase
        val uriReturn=when(uriMatcher.match(uri)){
            bookDir,bookItem->{
                val newBookId=db.insert("Book",null,values)
                Uri.parse("content://$authority/book/$newBookId")
            }
            categoryDir,categoryItem->{
                val newCategoryId=db.insert("Category",null,values)
                Uri.parse("content://$authority/category/$newCategoryId")
            }
            else->null
        }
        uriReturn
    }


    //更新
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    )=dbHelper?.let {
        val db = it.writableDatabase
        val updatedRows=when(uriMatcher.match(uri)){
            bookDir->db.update("Book",values,selection,selectionArgs)
            bookItem->{
                val bookId=uri.pathSegments[1]
                db.update("Book",values,"id = ?", arrayOf(bookId))
            }
            categoryDir->db.update("Category",values,selection,selectionArgs)
            categoryItem->{
                val categoryId=uri.pathSegments[1]
                db.update("Category",values,"id = ?", arrayOf(categoryId))
            }
            else ->null
        }
        updatedRows
    }?:0


    //删除数据
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?)=dbHelper?.let {
        val db=it.writableDatabase
        val deletedRows=when(uriMatcher.match(uri)){
            bookDir->db.delete("Book",selection,selectionArgs)
            bookItem->{
                val bookId=uri.pathSegments[1]
                db.delete("Book","id = ?", arrayOf(bookId))
            }
            categoryDir->db.delete("Category",selection,selectionArgs)
            categoryItem->{
                val categoryId=uri.pathSegments[1]
                db.delete("Category","id = ?", arrayOf(categoryId))
            }
            else -> 0
        }
        deletedRows
    }?:0


    override fun getType(uri: Uri)=when(uriMatcher.match(uri)){
        bookDir->"vnd.android.cursor.dir/vnd.com.workaholiclab.databasetest.provider.book"
        bookItem->"vnd.android.cursor.item/vnd.com.workaholiclab.databasetest.provider.book"
        categoryDir->"vnd.android.cursor.dir/vnd.com.workaholiclab.databasetest.provider.category"
        categoryItem->"vnd.android.cursor.item/vnd.com.workaholiclab.databasetest.provider.category"
        else->null
    }
}

```

> * 首先，在类开始的时候定义四个变量，分别访问表的所有数据，和表的弹跳数据
>
> * 然后在一个by lazy代码块力对UriMatcher进行初始化（by lazy代码块具体解析请见Bisic Knowledge）
>
>   * 将匹配的几种URI格式添加进去
>   * by lazy代码块是Kotlin提供的一种懒加载技术，只有当uriMatcher变量首次被调用的时候才会执行
>   * 并且代码块最后一行作为返回值赋给uriMatcher
>
> * 接下来是抽象方法的实现
>
>   * onCreate()利用一些语法糖操作，true表示ContentProvider创建成功，创建MyDatabaseHelper实例
>
>   * query()方法 获取SQLiteDatabase实例 ```val db = it.readableDatabase``` ,然后传入Uri参数判断访问哪张表```val cursor=when(uriMatcher.match(uri))```，再用query查询,将Cursor对象返回即可。
>
>     * 注意：当访问单条数据的时候，调用uri的getPathSegments()方法，它会将URI权限之后的部分以/符号分隔，并把分割后的结果让如第一个字符串列表中。
>
>     * 那么这个列表0位置放的是存放路径，第一个位置存放的是id
>
>     * ```kotlin
>       bookItem->{
>           val bookId=uri.pathSegments[1]
>           db.query("Book",projection,"id = ?", arrayOf(bookId),null, null,sortOrder)
>       }
>       ```
>
>   * insert()方法
>
>     * 这个很简单了
>
>     * ```kotlin
>       override fun insert(uri: Uri, values: ContentValues?)=dbHelper?.let {
>               val db=it.writableDatabase
>               val uriReturn=when(uriMatcher.match(uri)){
>                   bookDir,bookItem->{
>                       val newBookId=db.insert("Book",null,values)
>                       Uri.parse("content://$authority/book/$newBookId")
>                   }
>                   categoryDir,categoryItem->{
>                       val newCategoryId=db.insert("Category",null,values)
>                       Uri.parse("content://$authority/category/$newCategoryId")
>                   }
>                   else->null
>               }
>               uriReturn
>           }
>       ```
>
>     * 返回一个可以表示这条新增数据的Uri，Uri.parse方法，把URI解析成为Uri对象
>
>   * update()方法：
>
>     * ```kotlin
>       override fun update(
>              uri: Uri, values: ContentValues?, selection: String?,
>              selectionArgs: Array<String>?
>          )=dbHelper?.let {
>              val db = it.writableDatabase
>              val updatedRows=when(uriMatcher.match(uri)){
>                  bookDir->db.update("Book",values,selection,selectionArgs)
>                  bookItem->{
>                      val bookId=uri.pathSegments[1]
>                      db.update("Book",values,"id = ?", arrayOf(bookId))
>                  }
>                  categoryDir->db.update("Category",values,selection,selectionArgs)
>                  categoryItem->{
>                      val categoryId=uri.pathSegments[1]
>                      db.update("Category",values,"id = ?", arrayOf(categoryId))
>                  }
>                  else ->null
>              }
>              updatedRows
>          }?:0
>       ```
>
>     * **受影响的行数作为返回值**
>
>   * delete()方法：
>
>     * ```kotlin
>       override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?)=dbHelper?.let {
>              val db=it.writableDatabase
>              val deletedRows=when(uriMatcher.match(uri)){
>                  bookDir->db.delete("Book",selection,selectionArgs)
>                  bookItem->{
>                      val bookId=uri.pathSegments[1]
>                      db.delete("Book","id = ?", arrayOf(bookId))
>                  }
>                  categoryDir->db.delete("Category",selection,selectionArgs)
>                  categoryItem->{
>                      val categoryId=uri.pathSegments[1]
>                      db.delete("Category","id = ?", arrayOf(categoryId))
>                  }
>                  else -> 0
>              }
>              deletedRows
>          }?:0
>       ```
>
>       * **删除的行数作为返回值**
>
>   * getType()方法：
>
>     * 和前面介绍的一样，这里不再讲解了
>
>     * ```kotlin
>       override fun getType(uri: Uri)=when(uriMatcher.match(uri)){
>              bookDir->"vnd.android.cursor.dir/vnd.com.workaholiclab.databasetest.provider.book"
>              bookItem->"vnd.android.cursor.item/vnd.com.workaholiclab.databasetest.provider.book"
>              categoryDir->"vnd.android.cursor.dir/vnd.com.workaholiclab.databasetest.provider.category"
>              categoryItem->"vnd.android.cursor.item/vnd.com.workaholiclab.databasetest.provider.category"
>              else->null
>          }
>       ```

这样子，我们就将ContentProvider中的代码全部编写完了

**可以自己打开注册文件看看变化，这里不再过多阐述**

这里先把我们这个工程项目装到模拟器上面先

> 接下来，我们再写一个新项目ProviderTest来访问上面这个应用程序的数据

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <Button
        android:id="@+id/addData"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Add To Book"/>
    <Button
        android:id="@+id/queryData"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Query From Book"/>
    <Button
        android:id="@+id/updateData"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Update Book"/>
    <Button
        android:id="@+id/deleteData"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Delete From Book"/>


</LinearLayout>
```

```kotlin
package com.workaholiclab.providertest

import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.contentValuesOf
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var bookId:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addData.setOnClickListener {
            //添加数据
            val uri= Uri.parse("content://com.workaholiclab.databasetest.provider/book")
            val values=contentValuesOf("name" to "A Clash of Kings","author" to " George Martin","pages" to 1040,"price" to 22.85)
            val newUri=contentResolver.insert(uri,values)//insert方法会返回一个Uri对象，这个对象包含了新增数据的id
            bookId=newUri?.pathSegments?.get(1)//我们通过getPathSegments方法将这个id取出，稍后会用到
        }
        queryData.setOnClickListener {
            //查询数据
            val uri = Uri.parse("content://com.workaholiclab.databasetest.provider/book")
            contentResolver.query(uri,null,null,null,null)?.apply {
                while(moveToNext()){
                    val name=getString(getColumnIndex("name"))
                    val author=getString(getColumnIndex("author"))
                    val pages=getString(getColumnIndex("pages"))
                    val price=getString(getColumnIndex("price"))
                    println("$name $author $pages $price")
                }
                close()
            }
        }

        updateData.setOnClickListener {
            //更新数据
            bookId?.let {
                val uri=Uri.parse("content://com.workaholiclab.databasetest.provider/book/$it")
                val values= contentValuesOf("name" to "A storm of Swords","pages" to 1216,"price" to 24.05)
                contentResolver.update(uri,values,null,null)
            }
        }

        deleteData.setOnClickListener {
            //删除数据
            bookId?.let {
                val uri= Uri.parse("content://com.workaholiclab.databasetest.provider/book/$it")
                contentResolver.delete(uri,null,null)
            }
        }
    }
}
```



> 我承认，这些代码确实存在一些问题，需要大家自行做出修订，锻炼一下大家的Debug能力吧



> ContentProvider完更



# 运用手机多媒体

> 2021.2.24
>
> Gary哥哥的哥哥

> 运用手机多媒体丰富你的程序

> Android提供了一系列的相关API，使得我们在程序当中可以调用很多手机的多媒体资源，从而编写出更加丰富的应用

## 程序运行在Android手机上

> 在正式讲解之前，我们先来了解一下，如何将程序运行在Android手机上

这个很简单啦，USB连电脑，手机开发者选项开启调试，然后运行Android Studio的项目代码即可安装到手机上了

> 下面我们对几个常用的多媒体进行一一讲解

## 使用通知

> 这是一个很有特色的功能，连iOS5.0页引入了类似的功能
>
> 即便当应用程序不在前台运行的时候，借助通知来发送提示用户

### 通知渠道

> Android 8.0开始，每一个通知都要属于一个相应的渠道，每个应用程序都可以自由地创建当前应用拥有哪些通知渠道，但这些渠道的掌控权在用户的手上。用户可以自由地选择这些通知渠道的重要程度，是否响铃，是否震动或者是否要关闭这个渠道

> 为此，我们不用再担心一些垃圾通知的骚扰了
>
> * **下面我们来看看创建自己的通知渠道的详细步骤吧**

* 首先需要一个NotificationManager对通知进行管理，可以调用Contenx的getSystemService()方法来获取

  * getSystemService()

    * p:接收一个字符串参数用于确定获取系统的哪个服务，这里我们传入Context.NOTIFICATION_SERVICE即可

  * 写法如下

    ```kotlin
    val manager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    ```

* 接下来需要使用NotificationCannel类构件一个通知渠道，并调用NotificationManager的createNotificationChannel()方法完成构建

  * 由于这些方法都是Android8.0新增的API，我们调用的时候还需要进行版本的判断

    ```kotlin
    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
        val channel = NotificationChannel(channelId, channelName, importance)
        manager. createNotificationChannel(channel)
    }
    ```

* > **==我这里重点提醒提醒，VERSION_CODES.O（是O不是0）==**

* 

* 创建一个通知渠道至少需要知道渠道的ID，渠道名称和重要性三个删除

  * 渠道的ID可以随便定义，只要保证全局唯一性即可
  * 渠道名称是个用户看的，需要可以清楚地表达这个渠道的用途
  * 通知重要性等级从高到低：IMPORTANCE_HIGH / DEFAULT / LOW / MIN
    * 用户可以随意更改通知的重要性等级，开发者是无法干预的

### 通知的基本用法

> 通知的使用方法还是比较灵活的，可以创建在Activity，BroadcastReceiver 和Service当中（Service后面会学习到）。一般创建场景在后面两个常见，Activity里创建通知还是比较少的，因为一般只有当程序进入后台的时候才需要使用通知

> 我们先来学习一下创建通知的一般步骤

1. 首先需要一个Builder构造器来创建Notification对象，AndroidX库提供了一些兼容之前版本的API

* AndroidX提供了一个NotificationCompat类，使用这个类来构建Notification对象就不存在兼容性的问题了

  ```kotlin
  val notification = NotificationCompat.Builder(context, channelId).build()
  ```

  * NotificationCompat.Builder的构造函数中，p1是一个context；p2是渠道的ID
    * 需要我们在创建通知渠道时指定的渠道ID相匹配才行

* 上述代码只是创建了一个空的Notification对象，并没有什么实际的作用， 我们可以在最终的build()方法中连缀人一多的设置方法来创建一个丰富的Notification对象，先来看看一些基本的操作：

  ```kotlin
  val notification=NotificationCompat.Builder(context,channelId)
  	.setContentTitle("This is content title")
      .setContentText("This is content text").setSmallIcon(R.drawable.ic_launcher_background)
      .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_background))
      .build()
  ```

  > 这里一共调用了四个设置方法：
  >
  > * 设置Title
  > * 设置正文内容（下拉状态栏也可以看到这部分的内容）
  > * 设置通知的小图标
  >   * **注意：这里使用纯alpha图层的图片进行设置，小图标会显示在状态栏上方出现**
  > * 设置大图标，下拉状态栏可以看到大图标

  2. 以上工作全部完成过后，只需要调用NotificationManager的notify()方法就可以放通知显示出来

     * notify()方法接受两个参数：

       * p1:id，保证为每个通知指定的id都是不相同的

       * p2:Notification对象，这里我们将我们刚刚创建的Notification对象传入即可，因此显示一个通知就写成：

         ```kotlin
         manager.notify(1, notification)
         ```

  > 下面我们就以一个完整的例子NotificationTest项目来看看通知到底是长什么样的

activity_main.xml文件：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Send Notice"
        android:textAllCaps="false"
        android:id="@+id/sendNotice"/>

</LinearLayout>
```

MainActivity中：

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.0){
            val channel = NotificationChannel("normal","Normal",NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        sendNotice.setOnClickListener {
            val notification = NotificationCompat.Builder(this,"normal")
                .setContentTitle("This is content title")
                .setContentText("This is content text")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_launcher_background))
                .build()
            manager.notify(1,notification)
        }
    }
}
```

> 下面我们要使点击这条通知之后有效果，这里涉及新的概念PendingIntent
>
> * 和Intent有些类似

#### PendingIntent用法

> 它主要提供几个静态方法用于获取PendingIntent的实例

* getActivity()
* getBroadcast()
* getService

他们的参数都是：

* p1:Context
* 一般不用到，传入0即可
* p3:是一个Intent对象，通过这个对象构建出PendingIntent的意图
* p4:用于确定PendingIntent的行为,有FLAG_ONE_SHOT，FLAG_NO_CREATE，FLAG_CANCEL_CURRENT，FLAG_UPDATE_CURRENT，通常情况下传入0即可（至于上面这几个需要自行查阅文档）

> 下面来优化我们的NotificationTest项目

activity_notification.xml和NotificationActivity

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent" android:layout_height="match_parent">
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        android:textSize="24sp"
        android:text="This is notification layout"/>
</RelativeLayout>
```



MainActivity中：

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel = NotificationChannel("normal","Normal",NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        sendNotice.setOnClickListener {
            //构建意图
            val intent=Intent(this,NotificationActivity::class.java)
            val pi = PendingIntent.getActivity(this,0,intent,0)

            //注意下面setContentIntent(pi)
            //setAutoCancel(true) 点击之后自动消失
            val notification = NotificationCompat.Builder(this,"normal")
                .setContentTitle("This is content title")
                .setContentText("This is content text")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_launcher_background))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build()
            manager.notify(1,notification)
        }
    }
}
```

> 关于点击后自动消失，也可以在NotificationActivity中实现

```kotlin
class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(1)//这里的1就是那个通知设定的id
    }
}
```



#### 进阶技巧

> 实际上NotificationCompat.Builder中还提到了非常丰富的API，以便我们创建出更加多样的通知效果
>
> 但我们只讲解一些比较常用的API，其他API可以在大家实践的时候自行查阅资料

##### setStyle()方法

> 这个方法允许我们构建富文本的通知内容。也就是说，通知不光可以有文字和图标，还可以包含更多的内容

p：NotificationCompat.Style参数，这个参数就是用来构建具体的富文本信息，如长文字和图片等

ex：如果按我们之前讲的那样子写，如果通知的内容文字太多，他就只会缩略尾部的方式进行显示，而这里可以做到将全部东西显示出来

```kotlin
sendNotice.setOnClickListener {
            //构建意图
            val intent=Intent(this,NotificationActivity::class.java)
            val pi = PendingIntent.getActivity(this,0,intent,0)

            //注意下面setContentIntent(pi)
            //setAutoCancel(true) 点击之后自动消失
            val notification = NotificationCompat.Builder(this,"normal")
                .setContentTitle("This is content title")
//                .setContentText("　全国脱贫攻坚总结表彰大会开始，中共中央政治局常委、全国政协主席汪洋同志宣读《中共中央 国务院关于授予全国脱贫攻坚楷模荣誉称号的决定》。\n" +
//                        "\n" +
//                        "　　为隆重表彰激励先进，大力弘扬民族精神、时代精神和脱贫攻坚精神，充分激发全党全国各族人民干事创业的责任感、使命感、荣誉感，汇聚更强大的力量推进全面建设社会主义现代化国家，党中央、国务院决定，授予毛相林等10名同志，河北省塞罕坝机械林场等10个集体“全国脱贫攻坚楷模”荣誉称号。")
                .setStyle(NotificationCompat.BigTextStyle().bigText("　全国脱贫攻坚总结表彰大会开始，中共中央政治局常委、全国政协主席汪洋同志宣读《中共中央 国务院关于授予全国脱贫攻坚楷模荣誉称号的决定》。\n" +
                        "\n" +
                        "　　为隆重表彰激励先进，大力弘扬民族精神、时代精神和脱贫攻坚精神，充分激发全党全国各族人民干事创业的责任感、使命感、荣誉感，汇聚更强大的力量推进全面建设社会主义现代化国家，党中央、国务院决定，授予毛相林等10名同志，河北省塞罕坝机械林场等10个集体“全国脱贫攻坚楷模”荣誉称号。"))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_launcher_background))
                .setContentIntent(pi)
//                .setAutoCancel(true)
                .build()
            manager.notify(1,notification)
        }
```

![](E:\kotlin-study\Studying-Kotlin\Multimedia\setStyle().png)

> 除此之外还可以做到完整的显示一张图片

```kotlin
sendNotice.setOnClickListener {
            //构建意图
            val intent=Intent(this,NotificationActivity::class.java)
            val pi = PendingIntent.getActivity(this,0,intent,0)

            //注意下面setContentIntent(pi)
            //setAutoCancel(true) 点击之后自动消失
            val notification = NotificationCompat.Builder(this,"normal")
                .setContentTitle("This is content title")
//                .setContentText("　全国脱贫攻坚总结表彰大会开始，中共中央政治局常委、全国政协主席汪洋同志宣读《中共中央 国务院关于授予全国脱贫攻坚楷模荣誉称号的决定》。\n" +
//                        "\n" +
//                        "　　为隆重表彰激励先进，大力弘扬民族精神、时代精神和脱贫攻坚精神，充分激发全党全国各族人民干事创业的责任感、使命感、荣誉感，汇聚更强大的力量推进全面建设社会主义现代化国家，党中央、国务院决定，授予毛相林等10名同志，河北省塞罕坝机械林场等10个集体“全国脱贫攻坚楷模”荣誉称号。")
//                .setStyle(NotificationCompat.BigTextStyle().bigText("　全国脱贫攻坚总结表彰大会开始，中共中央政治局常委、全国政协主席汪洋同志宣读《中共中央 国务院关于授予全国脱贫攻坚楷模荣誉称号的决定》。\n" +
//                        "\n" +
//                        "　　为隆重表彰激励先进，大力弘扬民族精神、时代精神和脱贫攻坚精神，充分激发全党全国各族人民干事创业的责任感、使命感、荣誉感，汇聚更强大的力量推进全面建设社会主义现代化国家，党中央、国务院决定，授予毛相林等10名同志，河北省塞罕坝机械林场等10个集体“全国脱贫攻坚楷模”荣誉称号。"))
                .setContentText("Look, what a beautiful picture")
                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(resources,R.drawable.ic_launcher_background)))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_launcher_background))
                .setContentIntent(pi)
//                .setAutoCancel(true)
                .build()
            manager.notify(1,notification)
        }
```





> 需要注意的是，开发者只能再创建通知渠道的时候为他制定初始的重要等级，用户不认可，用户可以自行修改，因为通知渠道一旦创建就不能通过代码修改，我们再创建一条通知渠道来测试：

```kotlin
if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
    val channel = NotificationChannel("normal","Normal",NotificationManager.IMPORTANCE_DEFAULT)
    manager.createNotificationChannel(channel)
    val channel2=NotificationChannel("important","Important",NotificationManager.IMPORTANCE_HIGH)
    manager.createNotificationChannel(channel2)
}
```

```kotlin
sendNotice.setOnClickListener {
    //构建意图
    val intent=Intent(this,NotificationActivity::class.java)
    val pi = PendingIntent.getActivity(this,0,intent,0)

    //注意下面setContentIntent(pi)
    //setAutoCancel(true) 点击之后自动消失
    val notification = NotificationCompat.Builder(this,"important")
        .setContentTitle("This is content title")
    ...
}
```

搞个high给他，发现这里已经变成一个弹出式的通知了



## 调用摄像头和相册

### 调用摄像头拍照

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="take Photo"
        android:id="@+id/takePhotoBtn"/>

    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:id="@+id/imageView"/>


</LinearLayout>
```

MainActivity中：

> 下面代码确实比较长，**==详细的解析请见书本p369页==**

```kotlin
class MainActivity : AppCompatActivity() {
    val takePhoto = 1
    lateinit var imageUri :Uri
    lateinit var outputImage: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        takePhotoBtn.setOnClickListener {
            //创建File对象，用于存储拍照后的图片

            outputImage = File(externalCacheDir,"output_image.jpg")//采用应用管理目录来缓存（避免读写SD卡的危险权限）
            if(outputImage.exists()){
                outputImage.delete()
            }
            outputImage.createNewFile()
            imageUri = if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
                //将File对象转化成一个封装过的Uri对象（FileProvider对数据进行了保护）
                FileProvider.getUriForFile(this,"com.workaholiclab.cameraalbumtest.fileprovider",outputImage)
            }else{
                Uri.fromFile(outputImage) //该设备低于android7就调用Uri的fromFile方法将File转话为Uri对象
                //这个Uri对象包含了这张图片的真实存在的路径
            }
            //启动相机程序
            val intent =Intent("android.media.action.IMAGE_CAPTURE")
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)//指定图片的输入地址，这里为刚刚的Uri对象
            startActivityForResult(intent,takePhoto)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            takePhoto->{
                if (resultCode ==Activity.RESULT_OK){
                    //将拍摄的照片显示出来
                    val bitmap= BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))//将这张图片解析称为Bitmap对象
                    imageView.setImageBitmap(rotateIfRequired(bitmap))//变成ImageView，需要注意一些手机上拍照转化会发生一些旋转，需要处理一下
                }
            }
        }
    }

    //照片旋转处理
    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)
        return when(orientation){
            ExifInterface.ORIENTATION_ROTATE_90->rotateBitmap(bitmap,90)
            ExifInterface.ORIENTATION_ROTATE_180->rotateBitmap(bitmap,180)
            ExifInterface.ORIENTATION_ROTATE_270->rotateBitmap(bitmap,270)
            else-> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix =Matrix()
        matrix.postRotate(degree.toFloat())
        val rotateBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true)
        bitmap.recycle()//将不再需要的Bitmap对象回收
        return rotateBitmap
    }
}
```





> 下面不要忘记注册我们Provider了,FileProvider是一种特殊的Provider

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.workaholiclab.cameraalbumtest">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <provider
            android:authorities="com.workaholiclab.cameraalbumtest.fileprovider"
            android:name="androidx.core.content.FileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths"/>
        </provider>
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

> name属性是固定的
>
> authorities属性的值必须和刚才getUriForFile()方法的第二个参数一致
>
> <meta-data>指定Uri的分享路径，冰鞋引用了一个@xml/file_paths资源

file_paths.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path
        name="my_images"
        path="/"/>
</paths>
```

> name属性随便写
>
> path属性值表示共享的具体路径，一个/表示将整个SD卡进行分享，当然你也可以这分享存放上面图片的路径

### 从相册中选取照片

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="take Photo"
        android:id="@+id/takePhotoBtn"/>
    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="From Album"
        android:id="@+id/fromAlbumBtn"/>

    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:id="@+id/imageView"/>



</LinearLayout>
```

```kotlin
class MainActivity : AppCompatActivity() {
    val takePhoto = 1
    lateinit var imageUri :Uri
    lateinit var outputImage: File

    val fromAlbum = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        takePhotoBtn.setOnClickListener {
            //创建File对象，用于存储拍照后的图片

            outputImage = File(externalCacheDir,"output_image.jpg")//采用应用管理目录来缓存（避免读写SD卡的危险权限）
            if(outputImage.exists()){
                outputImage.delete()
            }
            outputImage.createNewFile()
            imageUri = if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
                //将File对象转化成一个封装过的Uri对象（FileProvider对数据进行了保护）
                FileProvider.getUriForFile(this,"com.workaholiclab.cameraalbumtest.fileprovider",outputImage)
            }else{
                Uri.fromFile(outputImage) //该设备低于android7就调用Uri的fromFile方法将File转话为Uri对象
                //这个Uri对象包含了这张图片的真实存在的路径
            }
            //启动相机程序
            val intent =Intent("android.media.action.IMAGE_CAPTURE")
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)//指定图片的输入地址，这里为刚刚的Uri对象
            startActivityForResult(intent,takePhoto)
        }

        //选取相册
        fromAlbumBtn.setOnClickListener {
            //打开文件选择器
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            //指定只显示图片
            intent.type ="image/*"
            startActivityForResult(intent,fromAlbum)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            takePhoto->{
                if (resultCode ==Activity.RESULT_OK){
                    //将拍摄的照片显示出来
                    val bitmap= BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))//将这张图片解析称为Bitmap对象
                    imageView.setImageBitmap(rotateIfRequired(bitmap))//变成ImageView，需要注意一些手机上拍照转化会发生一些旋转，需要处理一下
                }
            }

            fromAlbum->{
                if(resultCode == Activity.RESULT_OK && data != null){
                    data.data?.let {
                        uri ->
                        //将选择的图片显示
                        val bitmap=getBitmapFromUri(uri)
                        imageView.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver
        .openFileDescriptor(uri,"r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }

    //照片旋转处理
    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)
        return when(orientation){
            ExifInterface.ORIENTATION_ROTATE_90->rotateBitmap(bitmap,90)
            ExifInterface.ORIENTATION_ROTATE_180->rotateBitmap(bitmap,180)
            ExifInterface.ORIENTATION_ROTATE_270->rotateBitmap(bitmap,270)
            else-> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix =Matrix()
        matrix.postRotate(degree.toFloat())
        val rotateBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true)
        bitmap.recycle()//将不再需要的Bitmap对象回收
        return rotateBitmap
    }
}
```

> 需要注意的是，一般的程序对加载的图片压缩，这样子是为了避免直接加载到内存崩溃，好的做法是先压缩，这里大家可以自行查阅相关资料了



### 播放多媒体文件

> Android MediaPlayer 常用方法介绍 
>
> 方法：create(Context context, Uri uri) 
> 解释：静态方法，通过Uri创建一个多媒体播放器。 
>
> 方法：create(Context context, int resid) 
> 解释：静态方法，通过资源ID创建一个多媒体播放器 
>
> 方法：create(Context context, Uri uri, SurfaceHolder holder) 
> 解释：静态方法，通过Uri和指定 SurfaceHolder 【抽象类】 创建一个多媒体播放器 
>
> 方法： getCurrentPosition() 
> 解释：返回 Int， 得到当前播放位置 
>
> 方法： getDuration() 
> 解释：返回 Int，得到文件的时间 
>
> 方法：getVideoHeight() 
> 解释：返回 Int ，得到视频的高度 
>
> 方法：getVideoWidth() 
> 解释：返回 Int，得到视频的宽度 
>
> 方法：isLooping() 
> 解释：返回 boolean ，是否循环播放 
>
> 方法：isPlaying() 
> 解释：返回 boolean，是否正在播放 
>
> 方法：pause() 
> 解释：无返回值 ，暂停 
>
> 方法：prepare() 
> 解释：无返回值，准备同步 
>
> 方法：prepareAsync() 
> 解释：无返回值，准备异步 
>
> 方法：release() 
> 解释：无返回值，释放 MediaPlayer 对象 
>
> 方法：reset() 
> 解释：无返回值，重置 MediaPlayer 对象 
>
> 方法：seekTo(int msec) 
> 解释：无返回值，指定播放的位置（以毫秒为单位的时间） 
>
> 方法：setAudioStreamType(int streamtype) 
> 解释：无返回值，指定流媒体的类型 
>
> 方法：setDataSource(String path) 
> 解释：无返回值，设置多媒体数据来源【根据 路径】 
>
> 方法：setDataSource(FileDescriptor fd, long offset, long length) 
> 解释：无返回值，设置多媒体数据来源【根据 FileDescriptor】 
>
> 方法：setDataSource(FileDescriptor fd) 
> 解释：无返回值，设置多媒体数据来源【根据 FileDescriptor】 
>
> 方法：setDataSource(Context context, Uri uri) 
> 解释：无返回值，设置多媒体数据来源【根据 Uri】 
>
> 方法：setDisplay(SurfaceHolder sh) 
> 解释：无返回值，设置用 SurfaceHolder 来显示多媒体 
>
> 方法：setLooping(boolean looping) 
> 解释：无返回值，设置是否循环播放 
>
> 事件：setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener listener) 
> 解释：监听事件，网络流媒体的缓冲监听 
>
> 事件：setOnCompletionListener(MediaPlayer.OnCompletionListener listener) 
> 解释：监听事件，网络流媒体播放结束监听 
>
> 事件：setOnErrorListener(MediaPlayer.OnErrorListener listener) 
> 解释：监听事件，设置错误信息监听 
>
> 事件：setOnVideoSizeChangedListener(MediaPlayer.OnVideoSizeChangedListener listener) 
> 解释：监听事件，视频尺寸监听 
>
> 方法：setScreenOnWhilePlaying(boolean screenOn) 
> 解释：无返回值，设置是否使用 SurfaceHolder 显示 
>
> 方法：setVolume(float leftVolume, float rightVolume) 
> 解释：无返回值，设置音量 
>
> 方法：start() 
> 解释：无返回值，开始播放 
>
> 方法：stop() 
> 解释：无返回值，停止播放

我们先来梳理一下MediaPlayer的工作流程

1. 创建MediaPlayer对象
2. 调用setDataSource()方法设置音频文件的路径
3. 再调用prepare()方法进入准备状态
4. start(),pause(),reset()方法

> 我们来看一个很简单的例子吧

> **==这部分比较简单不再展开来说了，有兴趣自己看一下书p374页即可==**



#### 播放视频

> VedioView和MediaPlayer很类似，其主要方法如下：
>
> - setVideoPath：设置要播放的视频文件的位置
> - start：开始或继续播放视频
> - pause：暂停播放视频
> - resume：将视频从头开始播放
> - seekTo：从指定的位置开始播放视频
> - isPlaying：判断当前是否正在播放视频
> - getCurrentPosition：获取当前播放的位置
> - getDuration：获取载入的视频文件的时长
> - setVideoPath(String path)：以文件路径的方式设置VideoView播放的视频源
> - setVideoURI(Uri uri)：以Uri的方式设置视频源，可以是网络Uri或本地Uri
> - setMediaController(MediaController controller)：设置MediaController控制器
> - setOnCompletionListener(MediaPlayer.onCompletionListener l)：监听播放完成的事件
> - setOnErrorListener(MediaPlayer.OnErrorListener l)：监听播放发生错误时候的事件
> - setOnPreparedListener(MediaPlayer.OnPreparedListener l)：监听视频装载完成的事件

> **==同样，这一部分比较简单详见书本p378页即可==**



> 多媒体部分完更

# Service

> 2021.2.25
>
> Gary哥哥的哥哥的哥哥

> 后台默默的劳动者--Service

> 实现后台功能的Service属于四大组件之一，其重要性不言而喻

## Service是什么

> Service是Android中实现程序后台运行的解决方案
>
> 它非常适合执行哪些不需要和用户交互而且还要求长期运行的任务。
>
> Service的运行不依赖任何见面，即使程序被奇幻到后台，Service仍能够保持正常运行

> 不过需要注意的是，Service并不是运行在一个独立的进程当中的，而是依赖于创建Service时所在的应用程序进程。
>
> 当某个应用程序进程被杀掉时，所有依赖于该进程的Service也会停止运行

> 实际上，Service并不会自动开启线程，所有的代码都是默认运行在主线程当中的。
>
> 也就是说，我们需要在Service的内部的手动创建子线程，并在这里执行具体的任务，否则就有可能出现主线程被阻塞的情况。

## Android多线程编程

> 如果你熟悉Java，多线程想必不会陌生，一些比较耗时间的操作都会放在子线程当中运行，避免主线程被阻塞

### 线程的基本用法

> Android的多线程和Java有着很相似的语法
>
> * **一个线程继承Thread，然后重写父类的run()方法**

```kotlin
class MyThread : Thread(){
    override fun run(){
        //逻辑代码
    }
}
```

> **当然，启动这个线程只需要**
>
> ```kotlin
> MyThread().start()
> ```

> * **上面这种方式耦合性有点高，我们可能更多的使用实现Runnable接口的方式来定义一个线程：**

```kotlin
class MyThread : Runnable{
    override fun run(){
        //逻辑代码
    }
}

//启动线程：
val myThread = MyThread()
Thread(myThread).start()
```

> 可以看到Thread的构造函数接收了一个Runnable参数，接着调用Thread的start方法即可
>
> **当然如果你不想实现一个Runnable接口，那用Lambda表达式的写法也行**

```kotlin
Thread{
    //编写逻辑代码
}.start()
```



> **上面这些方法在java中都可以看到，下面来个Kotlin特别的地方**
>
> * **一个简单的开启线程的方式：**

```kotlin
thread {
    //编写逻辑代码
}
```

> 这里的thread是一个在Kotlin内置的鼎城函数，这里连start方法都不用调用了

### 在子线程中更新UI

> Android的UI也是也是线程不安全的，也就是说，**如果想要更新应用程序里的UI元素，必须在主线程中进行**，否则就会出现异常

> * 下面我们用一个例子AndroidThreadTest来验证一下

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Change Text"
        android:id="@+id/changeTextBtn"/>
    
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        android:text="Hello world"
        android:textSize="20sp"
        android:id="@+id/textView"/>

</RelativeLayout>
```

> 需要注意的是，Android是不允许在子线程更新相应的UI控件的，但我们有的耗时操作如何处理好呢？
>
> ==对于这种情况Android提供了一套异步消息的处理机制，完美的解决在子线程中进行UI操作的问题，我们在下一小节去分析==

MainActivity:

```kotlin
class MainActivity : AppCompatActivity() {
    val updateText = 1
    val handler = object : Handler(){
        override fun handleMessage(msg: Message) {
            //可以在这里进行UI操作
            when(msg.what){
                updateText ->textView.text = "Nice to meet you"
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        changeTextBtn.setOnClickListener {
            thread {
                val msg = Message()
                msg.what = updateText
                handler.sendMessage(msg)//将Message对象发送出去
            }
        }
    }
}
```

> 这次我们没有在子线程当中进行UI操作
>
> 而是将它的what字段变成updateText
>
> 然后调用Handler的sendMessage()方法将这条Message发送出去，很快Handler就会收到这条message
>
> 重写hanleMessage方法
>
> **注意：此时的handleMessage()方法就是在主线程当中运行的了，可以放心使用**
>
> * 这就是Android异步消息处理的基本用法，出色的解决了在子线程中更新UI的问题
>   * 下面我们对异步消息处理机制进行进一步的分析



### 异步消息处理机制

> Android中的异步消息处理主要由四个部分组成： Message ，Handle ， MessageQueue 和 Looper
>
> * 这里其中Message和Handler已经在上一小节接触过了
>
> 下面我们仍对这四个部分分开来讲解一下

#### Message

> Message 是在线程之间传递的消息，它可以在内部携带少量的信息，用于在不同线程之间交换数据。通常使用 Message 的 what 字段携带命令，除此之外还可以使用 arg1 和arg2 字段来携带一些整形数据，使用 obj 字段携带一个 Object 对象。





#### Handler

> Handler 顾名思义也就是处理者的意思，它主要是用于发送和处理消息的。发送消息一般是使用 Handler 的 sendMessage()方法，而发出的消息经过一系列地辗转处理后，最终会传递到 Handler 的 handlerMessage()方法中。





#### MessageQueue

> MessageQueue 是消息队列的意思，它主要用于存放所有通过 Handler 发送的消息。这部分消息会一直存在于消息队列中，等待被处理。每个线程中只会有一个 MessageQueue 对象。



#### Looper

> Looper 是每个线程中的 MessageQueue 的管家，调用 Looper 的 loop()  方法后，就会进入到一个无限循环当中，然后每当发现 MessageQueue 中存在一条消息，就会将它取出，并传递到 Handler 的  handleMessage() 方法中。每个线程中也只会有一个 Looper 对象。

#### 核心步骤



* 首先需要在主线程当中创建一个 Handler 对象，并重写 handleMessage() 方法。

* 然后当子线程中需要进行UI操作时，就创建一个 Message 对象，并通过 Handler 将这条消息发送出去。

* 之后这条消息会被添加到 MessageQueue 的队列中等待被处理，而 Looper 则会一直尝试从 MessageQueue 中取出待处理消息

* 最后分发回 Handler 的 handleMessage() 方法中。

* 由于 Handler 是在主线程中创建的，所以此时 handleMessage() 方法中的代码也会在主线程中运行，于是就可以安心地进行UI操作了。

![img](https://images2015.cnblogs.com/blog/875028/201601/875028-20160131015607396-1502069272.jpg)

**一条 Message 经过这样一个流程的辗转调用后，也就从子线程进入到了主线程，从不能更新 UI 变成了可更新 UI，整个异步消息处理的核心思想也就如此。**



#### 使用AsyncTask

> 为了更加**方便我们在子线程中对 UI 进行操作**，**Android 还提供了另外一些好用的工具，AsyncTask 就是其中之一。**借助  AsyncTask，即使你对异步消息处理机制完全不了解，**也可以十分简单地从子线程切换到主线程。**当然，==**AsyncTask  背后的实现原理也是基于异步消息处理机制的，只是 Android 做了很好的封装而已。**==

于 AsyncTask 是一个抽象类，所以如果我们想使用它，就必须创建一个子类去继承它。在继承时我们可以为 AsyncTask 类指定三个泛型参数，这三个参数的用途如下：

* **Params：**在执行 AsyncTask 时需要传入的参数，可用于在后台任务中使用。
* **Progress：**后台任务执行时，如果需要在界面上显示当前的进度，则使用这里指定的泛型作为进度单位。
* **Result：**当任务执行完毕后，如果需要对结果进行返回，则使用这里指定的泛型作为返回值类型。

ex:

```kotlin
class DownloadTask : AsyncTask<Unit,Int,Bollean>(){
    
}
```

> 接着我们还需要**重写 AsyncTask 中的几个方法**才能完成对任务的定制。

* onPreExecute():　这个方法会在后台任务开始执行之前调用，用于进行一些界面上的初始化操作，比如显示一个进度条对话框等。
* **doInBackground(Params...)：****这个方法中的所有代码都会在子线程中运行**，我们应该在这里去**处理所有的耗时任务**。注意，在这**个方法中是不可以进行 UI 操作的。
* **onProgressUpdate(Progress...)：**当后台任务中调用了 publishProgress(Progress...)方法后，这个方法就会很快被调用，方法中携带的参数就是在后台任务中传递过来的。**在这个方法中可以对 UI 进行操作，利用参数中的数值就可以对界面元素进行相应地更新。**
* **onPostExecute(Result)：**当后台任务执行完毕并通过 return 语句进行返回时，这个方法就很快会被调用。返**回的数据会作为参数传递到此方法中，可以利用返回的数据来进行一些 UI 操作，**比如提醒任务执行的结果，以及关闭掉进度条对话框等。

> * **==需要注意的是，Android其实确实已经把AsyncTask弃用了，具体原因可以自己上网查阅资料，但这里为了讲解知识点，我们还是强行搞一下==**
>
> ex:

```kotlin
class DownloadTask : AsyncTask<Unit,Int,Boolean>() {

    override fun onPreExecute() {
        progressDialog.show()//显示进度对话框
        
    }

    override fun doInBackground(vararg params: Unit?)=try{
        while (true){
            val downloadPercent = doDownload()//这是一个虚构的方法
            publishProgress(downloadPercent)
            if (downloadPercent>=100){
                break
            }
        }
        true
    }catch (e:Exception){
        e.printStackTrace()
        false
    }

    override fun onProgressUpdate(vararg values: Int?) {
        //在这里更新下载进度
        progressDialog.setMessage("Downloaded ${values[0]}%")
    }

    override fun onPostExecute(result: Boolean) {
        progressDialog.dismiss()//关闭进度条
        //这里提示下载结果
        if (result){
            Toast.makeText(context,"Succeeded",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
        }
    }
}
```

> 诀窍在于：
>
> doInBackground子线程中执行耗时任务，但不能改UI
>
> onProgressUpdate改UI
>
> onPostExecute收尾

想启动这个程序只需要：

```kotlin
DownloadTask().execute()
//execute()方法可以传入任意数量的参数，这些参数传递到doInBackground()方法当中
```



## Service的基本用法

> 新建一个ServiceTest项目来进行测试，**==由于此部分流程的图片较多，所以具体内容自己建书本p399页，这里只展示重点代码==**

### 定义一个Service

```kotlin
class MyService : Service() {
    //onBind方法我们在后面的小节再做讲解
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
```

> * onCreate()：创建Service时候调用
> * onStartCommand()方法在每次Service启动的时候调用
> * onDestroy()方法在Service销毁时候调用

> 通常情况下，如果我们希望Service一旦启动就立即去执行某些动作，就可以将逻辑卸载onStartCommand()方法里面。而当Service销毁时候，我们又要在OnDestroy()方法当中回收那些不再使用的资源
>
> 每个Service**都需要在注册文件中注册才能生效**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.workaholiclab.servicetest">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <service
            android:name=".MyService"
            android:enabled="true"
            android:exported="true"></service>

        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

### 启动和停止Service

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Start Service"
        android:id="@+id/startServiceBtn"/>

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Stop Service"
        android:id="@+id/stopServiceBtn"/>
</LinearLayout>
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startServiceBtn.setOnClickListener {
            val intent = Intent(this,MyService::class.java)
            startService(intent)//启动Service
        }
        stopServiceBtn.setOnClickListener {
            val intent = Intent(this,MyService::class.java)
            stopService(intent)//停止Service
        }
    }
}
```

```kotlin
class MyService : Service() {
    //onBind方法我们在后面的小节再做讲解
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyService","onCreate executed")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyService","onStartCommand executed")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyService","onDestroy executed")
    }
}

```



### Activity和Service的通信

> 在上一小节可能你也发现了，Service是在Activity中启动的。但是启动之后，Activity并不知道Service干了什么，只能叫他stop。
>
> 如果让Activity知道Service干了什么，指挥Service干什么，就要用到onBind()方法了，这力我们希望MyService实现一个下载的功能，Activity决定什么时候开始，随时查看下载进度

```kotlin
class MyService : Service() {

    private val mBinder = DownloadBinder()

    class DownloadBinder : Binder() {
        fun startDownload(){
            Log.d("MyService", "startDownload executed")
        }
        fun getProgress():Int{
            Log.d("MyService","getProgress executed")
            return 0
        }
    }

    override fun onBind(intent: Intent): IBinder {
       return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyService","onCreate executed")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyService","onStartCommand executed")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyService","onDestroy executed")
    }
}

```

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Start Service"
        android:id="@+id/startServiceBtn"/>

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Stop Service"
        android:id="@+id/stopServiceBtn"/>
    
    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Bind Service"
        android:id="@+id/bindServiceBtn"/>
    
    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Unbind Service"
        android:id="@+id/unbindServiceBtn"/>
</LinearLayout>
```

```kotlin
class MainActivity : AppCompatActivity() {
    lateinit var  downloadBinder : MyService.DownloadBinder

    //ServiceConnection的匿名类
    private val connection = object : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            downloadBinder = service as MyService.DownloadBinder
            downloadBinder.startDownload()
            downloadBinder.getProgress()
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startServiceBtn.setOnClickListener {
            val intent = Intent(this,MyService::class.java)
            startService(intent)//启动Service
        }
        stopServiceBtn.setOnClickListener {
            val intent = Intent(this,MyService::class.java)
            stopService(intent)//停止Service
        }

        bindServiceBtn.setOnClickListener {
            val intent = Intent(this,MyService::class.java)
            bindService(intent,connection, Context.BIND_AUTO_CREATE)//绑定Service
        }

        unbindServiceBtn.setOnClickListener {
            unbindService(connection)//解绑Service
        }
    }
}
```

> 在成功绑定的时候调用

```kotlin
onServiceConnected()
```



## Service的生命周期

> 实际上每次调用一个startService()方法，就有多少次onStartCommand()执行，但Service只会存在一个实例
>
> **还可以调用Context的bindService()来获取一个Service的持久链接，这是就会毁掉Service的onBind()方法，若还没创建，则会在此之前先调用onCreate()方法，之后调用onBind返回Ibinder对象实例，这样子就能和Service进行通信了**
>
> stopService和unbindService同时调用才会执行onDestroy方法

## 更多Service的小技巧

> Service还存在众多的小技巧



### 前台Service

> 如果你想Service一直保持运行状态，没有回收的风险，那就要使用前台Service了，它最大的区别就是下拉状态栏的通知会常在

下面我们来实现一下：

> 修改MyService的onCreate()方法：

```kotlin
class MyService : Service() {

    private val mBinder = DownloadBinder()

    class DownloadBinder : Binder() {
        fun startDownload(){
            Log.d("MyService", "startDownload executed")
        }
        fun getProgress():Int{
            Log.d("MyService","getProgress executed")
            return 0
        }
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d("MyService","onBind executed")
       return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyService","onCreate executed")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel = NotificationChannel("my_service","前台Service通知",NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        val intent =Intent(this,MainActivity::class.java)
        val pi =PendingIntent.getActivity(this,0,intent,0)
        val notification = NotificationCompat.Builder(this,"my_service")
            .setContentTitle("This is the content title")
            .setContentText("This is the content text")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_launcher_background))
            .setContentIntent(pi)
            .build()
        startForeground(1,notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyService","onStartCommand executed")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyService","onDestroy executed")
    }
}
```



> 这里和前面的Notification很类似，只不过我们没有使用NotificationManager将通知显示出来，而是

```kotlin
startForeground(1,notification)
```

p1:通知的id，类似于notify()方法的第一个参数

p2:构建Notification对象

startForeground(1,notification)方法会让**MyService变成一个前台Service并在系统状态栏显示出来**

> Android 9.0 后，前台Service还需要在注册文件注册
>
> * 对了，前台Service在Android 8.0就有了

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.workaholiclab.servicetest">
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
    ......
```

### 使用IntentService

> 如果直接在Sercice里处理一些耗时的逻辑，就很容易出现ANR（Application Not Responding）

这时候，Android的多线程技术，我们应该在Service的每个具体的方法里面开启一个子线程，然后在这里处理那些耗时的逻辑，因此一个标准的Service改成写法如下：



```kotlin
override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Log.d("MyService","onStartCommand executed")
    thread { 
        //处理具体的耗时逻辑
    }
    return super.onStartCommand(intent, flags, startId)
    
}
```

> 但这种Service一旦启动就会一直处于运行状态，必须调用stopService()或stopSelf()方法或者被系统回收Service才会停止，想让一个Service在执行完毕之后就停止

```kotlin
override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Log.d("MyService","onStartCommand executed")
    thread {
        //处理具体的耗时逻辑
        stopSelf()
    }
    return super.onStartCommand(intent, flags, startId)
}
```



> 为了进一步我们管理Service，Android专门提供了一个IntentService方法，很好解决了程序员忘记开启线程，或者忘记调用stopSelf()方法

新建一个MyIntentService类：

```kotlin
class MyIntentService:IntentService("MyIntentService") {
    override fun onHandleIntent(intent: Intent?) {
        //打印当前线程的id
        Log.d("MyIntentService","Thread id is ${Thread.currentThread().name}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyIntentService","onDestroy executed")
    }

}
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Start Service"
        android:id="@+id/startServiceBtn"/>

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Stop Service"
        android:id="@+id/stopServiceBtn"/>

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Bind Service"
        android:id="@+id/bindServiceBtn"/>

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Unbind Service"
        android:id="@+id/unbindServiceBtn"/>

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Start IntentService"
        android:id="@+id/startIntentServiceBtn"/>
</LinearLayout>
```

```kotlin
class MainActivity : AppCompatActivity() {
    lateinit var  downloadBinder : MyService.DownloadBinder

    //ServiceConnection的匿名类
    private val connection = object : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            downloadBinder = service as MyService.DownloadBinder
            downloadBinder.startDownload()
            downloadBinder.getProgress()
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startServiceBtn.setOnClickListener {
            val intent = Intent(this,MyService::class.java)
            startService(intent)//启动Service
        }
        stopServiceBtn.setOnClickListener {
            val intent = Intent(this,MyService::class.java)
            stopService(intent)//停止Service
        }

        bindServiceBtn.setOnClickListener {
            val intent = Intent(this,MyService::class.java)
            bindService(intent,connection, Context.BIND_AUTO_CREATE)//绑定Service
        }

        unbindServiceBtn.setOnClickListener {
            unbindService(connection)//解绑Service
        }
        
        startIntentServiceBtn.setOnClickListener { 
            //打印主线程id
            Log.d("MainActivity","Thread id is ${Thread.currentThread().name}")
            val intent = Intent(this,MyIntentService::class.java)
            startService(intent)
        }
    }
}
```



> 记得注册哦！！！

```xml
<service android:name=".MyIntentService"
            android:enabled="true"
            android:exported="true"/>
    </application>

</manifest>
```

> 但其实，MyIntentService已经被Java弃用了



> Service博客完更

# 使用网络技术

> Gary哥哥的哥哥
>
> 2021.2.26

> 这里我们使用网络技术丰富我们的应用程序，本章节主要讲解如何**在手机端使用HTTP和服务器进行网络交互，并对服务器返回的数据进行解析**

## WebView的用法

> 有时候我们有一些特殊的需求，比如在应用程序当中展示一些网页
>
> * 这里就要使用到WebView这个空间了

很简单的：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <WebView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/webView"/>

</LinearLayout>
```

```kotlin
package com.workaholiclab.webviewtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://1.semantic-ui.com/")

    }
}
```

> **最后记得注册哦！**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.workaholiclab.webviewtest">

    <uses-permission android:name="android.permission.INTERNET"/>
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

> 只要确保你的计算机可以上网就可以用模拟器弹出这个网址

> **下面我们需要对HTTP协议做一些真正的网络开发工作了！**

## 使用HTTP访问网络

> 这里HTTP全部的原理或许你要学习计算机网络专业课程或者网络开发才能学会，但这里作为Android开发，我们只会简单介绍。
>
> * **==不求甚解==**

> 前面其实就是我们像那个网站发出了一条HTTP的请求，它把网页的html代码返回回来，显示在我们的APP上

> 接下来我们重点看看通过手动发送HTTP请求的方式，更加深入地理解这个过程

### 使用HttpURLConnection

> 在android上面发送HTTP请求一般有两种方法，HttpURLConnection和HttpClient，不过由于后者的API过多，扩展过于困难，现在都一般建议我们用前者

* 首先，获取HttpUrlConnection实例，一般这需要创建一个URL对象，并且出传入目标的网络地址，然后调用一下openConnection()方法即可。

```kotlin
val url = URL("https://www.baidu.com/")
val connection = url.openConnection() as HttpURLConnection
```

* 在得到HttpURLConnection的实例之后，我们可以设置一下HTTP的请求方法，常用的是GET和POST
  * Get代表想获取数据
  * POST想提交数据

```kotlin
connection.requestMethod = "GET"
```

* 接下来就可以进行一些自由的定制了
  * 比如设置连接超时
  * 读取超时的毫秒数
  * 以及服务器希望的到的一些消息

```kotlin
connection.connectionTimeout = 8000
connection.readTimeout = 8000
```

* 之后再调用getInputStream()方法就可以获取到服务器返回的输入流，剩下的任务就是对输入流进行读取

```kotlin
val input = connection.inputStream
```

* 最后关闭这个连接

```kotlin
connection.disconnect()
```

> 下面新建一个项目来熟悉一下

别忘了注册先！！！

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Send Request"
        android:textAllCaps="false"
        android:id="@+id/sendRequestBtn"
        />

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:id="@+id/responseText"/>
    </ScrollView>

</LinearLayout>
```

```kotlin
package com.workaholiclab.networktest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendRequestBtn.setOnClickListener {
            sendRequestWithHttpURLConnection()
        }
    }

    private fun sendRequestWithHttpURLConnection() {
        //开启线程发起的网络请求
        thread {
            var connection: HttpURLConnection?=null
            try {
                val response = StringBuilder()
                val url = URL("https://www.baidu.com")
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout=8000
                connection.readTimeout=8000
                val input = connection.inputStream
                //下面对获取到的输入流进行读取
                val reader = BufferedReader(InputStreamReader(input))
                reader.use {
                    reader.forEachLine {
                        response.append(it)

                    }
                }
                println(response.toString())
                showResponse(response.toString())
            }catch (e:Exception) {
                e.printStackTrace()
            }finally {
                connection?.disconnect()
            }
        }
    }

    private fun showResponse(response: String) {
        runOnUiThread{
            //在这里进行UI操作
            responseText.text = response
        }
    }
}
```

> **如果想要提交数据给服务器也很简单** **如下所示即可：**
>
> * 向服务器提交用户名和密码：

```kotlin
connection.requestMethod = "POST"
val output = DataOutputStream(connection.outputStream)
output.writeBytes("username=admin&password=123456")
```

**注意每条数据都要以KV对的形式存在，数据与数据之间用&隔开**



## 使用OkHTTP

> 在开源如此盛行的今天，OkHttp已经成为Android开发者网络通讯库的首选

* 添加依赖文件

```xml
implementation 'com.squareup.okhttp3:okhttp:4.1.0'
```

> 至于你看博客的时候OkHttp库最新版本是多少，可以访问它的github主页

> 全部步骤如下面代码所示

```kotlin
//创建实例
val client = OkHttpClient()
//发起一条Http请求，创建一个Request对象
//val request = Request.Builder().build()//这样子只能创建一个空的request，应该像下面一样：
//赋值
val request = Request.Builder().url("https://www.baidu.com").build()
//newCall()来创建一个Call对象
val response=client.newCall(request).execute()
//Request对象就是服务器返回的数据了，我们采用如下写法来得到返回的数据
val responseData=response.body?.string()

//如果是POST的话会麻烦一点点，如下：
//先构建Request Body对象来存放待提交的数据
val requestBody = FormBody.Builder().add("username","admin").add("password","123456").build()
//调用post方法将RequestBody对象传入
val requestPost=Request.Builder().url("https://www.baidu.com").post(requestBody).build()
//后面就和Get一样调用execute()方法来发送并请求获取服务器返回的数据即可
```

> 下面来对上面之前那个项目做修改，用OkHttp库来做

```kotlin
private fun sendRequestWithOkHttp() {
    thread {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url("https://www.baidu.com").build()
            val response = client.newCall(request).execute()
            val responseData= response.body?.string()
            if(responseData!=null){
                showResponse(responseData)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}

private fun showResponse(response: String) {
    runOnUiThread{
        //在这里进行UI操作
        responseText.text = response
    }
}
```



## 解析XML格式数据

> 这里我们需要解决一个问题，这些数据到底是以什么样子的形式在网络上进行传输的呢？
>
> * 网络上传输数据的常用格式为XML和JSON
>
> * 下面我们先来看一下XML吧

搭建一个Web服务器非常简单，这里我们选择使用Apache服务器，如果你是Windows就要手动搭建，如果是Mac或者Linux的话默认是安装好的，只需要启动即可，具体方法可自行上网查阅，这里以Windows来讲解：



> **==详细下载步骤请见课本p436，或到网上搜索==**

```xml
<apps>
    <app>
        <id>1</id>
        <name>Google Maps</name>
        <version>1.0</version>
    </app>
    <app>
        <id>3</id>
        <name>Google Play</name>
        <version>2.3</version>
    </app>
</apps>
```

> 下面看看Android如何解析这段XML



### Pull解析方式

> 我们下面依然在OkHttpTest工程上继续修改
>
> * 下面这段代码确实比较烦人，耐心点领会



```kotlin
class MainActivity : AppCompatActivity() {
    private val keyMain="MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fun testOkHttp() {
            //创建实例
            val client = OkHttpClient()
            //发起一条Http请求，创建一个Request对象
            //val request = Request.Builder().build()//这样子只能创建一个空的request，应该像下面一样：
            //赋值
            val request = Request.Builder().url("https://www.baidu.com").build()
            //newCall()来创建一个Call对象
            val response=client.newCall(request).execute()
            //Request对象就是服务器返回的数据了，我们采用如下写法来得到返回的数据
            val responseData=response.body?.string()

            //如果是POST的话会麻烦一点点，如下：
            //先构建Request Body对象来存放待提交的数据
            val requestBody = FormBody.Builder().add("username","admin").add("password","123456").build()
            //调用post方法将RequestBody对象传入
            val requestPost=Request.Builder().url("https://www.baidu.com").post(requestBody).build()
            //后面就和Get一样调用execute()方法来发送并请求获取服务器返回的数据即可
        }

        sendRequestBtn.setOnClickListener {
            sendRequestWithOkHttp()
        }

    }

    private fun sendRequestWithOkHttp() {
        thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()

                    //指定访问服务器地址是计算机本机
                    .url("http://10.0.2.2/get_data.xml")
                    .build()
                val response = client.newCall(request).execute()
                val responseData= response.body?.string()
                if(responseData!=null){
//                    showResponse(responseData)
                    //解析XML
                    parseXMLWithPull(responseData)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun parseXMLWithPull(xmlData: String) {
        try{
            val factory = XmlPullParserFactory.newInstance()
            val xmlPullParser = factory.newPullParser()
            xmlPullParser.setInput(StringReader(xmlData))
            var eventType = xmlPullParser.eventType
            var id =""
            var name =""
            var version = ""
            while (eventType!=XmlPullParser.END_DOCUMENT){
                val nodeName = xmlPullParser.name
                when(eventType){
                    //开始解析某个节点
                    XmlPullParser.START_TAG->{
                        when(nodeName){
                            "id" -> id =xmlPullParser.nextText()
                            "name"-> name=xmlPullParser.nextText()
                            "version" -> version=xmlPullParser.nextText()
                        }
                    }
                    //完成某个节点的解析
                    XmlPullParser.END_TAG ->{
                        if("app"==nodeName){
                            Log.d(keyMain,"id is $id")
                            Log.d(keyMain,"name is $name")
                            Log.d(keyMain,"version is $version")
                        }
                    }
                }
                eventType=xmlPullParser.next()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun showResponse(response: String) {
        runOnUiThread{
            //在这里进行UI操作
            responseText.text = response
        }
    }


}
```

> 下面是核心代码的讲解，其他部分和OkHttp的内容是一样的

```kotlin
    private fun sendRequestWithOkHttp() {
        thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    //这里吧HTTP请求·的地址改成了下面这个xml
                    //指定访问服务器地址是计算机本机
                    .url("http://10.0.2.2/get_data.xml")
                    //10.0.2.2对于模拟器来说就是计算机本机的IP地址
                    .build()
                val response = client.newCall(request).execute()
                val responseData= response.body?.string()
                if(responseData!=null){
//                    showResponse(responseData)
                    //解析XML，不再用展示
                    parseXMLWithPull(responseData)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun parseXMLWithPull(xmlData: String) {
        try{
            //首先创建XmlPullParserFactory实例
            val factory = XmlPullParserFactory.newInstance()
            //接住实例得到XmlPullParser对象
            val xmlPullParser = factory.newPullParser()
            //调用setInput方法将服务器返回的XML数据设置进去
            xmlPullParser.setInput(StringReader(xmlData))
            //解析的过程当中可以通过getEventType获取当前解析的事件
            var eventType = xmlPullParser.eventType
            var id =""
            var name =""
            var version = ""
            //然后在while循环当中不断解析，如果解析不等于XmlPullParser.END_DOCUMENT说明解析工作还没有完成那个，调用next方法
            while (eventType!=XmlPullParser.END_DOCUMENT){
                //获取当前节点的名字
                val nodeName = xmlPullParser.name
                when(eventType){
                    //开始解析某个节点
                    XmlPullParser.START_TAG->{
                        when(nodeName){
                            //发现对应的就调用nextText方法来获取节点内的具体内容
                            "id" -> id =xmlPullParser.nextText()
                            "name"-> name=xmlPullParser.nextText()
                            "version" -> version=xmlPullParser.nextText()
                        }
                    }
                    //完成某个节点的解析
                    XmlPullParser.END_TAG ->{
                        //每当解析完一个app将其打印出来
                        if("app"==nodeName){
                            Log.d(keyMain,"id is $id")
                            Log.d(keyMain,"name is $name")
                            Log.d(keyMain,"version is $version")
                        }
                    }
                }
                eventType=xmlPullParser.next()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
```

> 为了能让程序使用HTTP，我们还需要进行如下配置
>
> * 在res文件目录下新建xml文件夹，新建一个network_config.xml文件

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system"/>
        </trust-anchors>
    </base-config>
</network-security-config>
```

> 这个文件的意思就是允许我们以明文的方式在网络上传播数据，而HTTP使用的就是明文传输

> 还有记得注册文件：（最后那句！）

```xml
<application
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:supportsRtl="true"
    android:theme="@style/AppTheme"
    android:networkSecurityConfig="@xml/network_config">
```



### SAX方法

> 虽然比Pull方法复杂一些，但是语义方面会更加清楚。

> **==具体内容见课本p441页！！！==**





## 解析JSON文件和GSON文件

> 这个在之前的SpringBoost和Java的课程就有所介绍，因此这里也不展开来讲了

> **==具体内容见课本p444页！！！==**

## 网络请求回调

> 其实我们之前的OkHttp和HttpURLConnection都是很有问题的，因为一个应用程序很可能会在许多地方都使用网络功能，而发送HTTP请求的代码基本是相同的，如果每次都编写，是很麻烦的。因此我们会写一些接口来解决。

```kotlin
object HttpUtil {
    fun sendHttpRequest(address: String):String{
        var connection: HttpURLConnection?=null
        try {
            val response = StringBuilder()
            val url = URL(address)
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout=8000
            connection.readTimeout=8000
            val input = connection.inputStream
            //下面对获取到的输入流进行读取
            val reader = BufferedReader(InputStreamReader(input))
            reader.use {
                reader.forEachLine {
                    response.append(it)

                }
            }
            return response.toString()
        }catch (e: Exception) {
            e.printStackTrace()
            return e.message.toString()
        }finally {
            connection?.disconnect()
        }
    }
}
```

> 因此，每当发起一条HTTP请求的时候就可以写成：

```kotlin
val address="https://www.baidu.com"
val response = HttpUtil.sendHttpRequest(address)
```

> ==但是网络请求一般情况下都是耗时的操作，而上面这个方法的内部，并没有开启一个线程，就可能导致主线程被阻塞==
>
> * 但这个方法里面开启一个线程来发起HTTP请求，**服务器响应的数据是没有办法进行返回的**，由于耗时逻辑在子线程里面，这个方法会在服务器还没来得及响应的时候就执行结束了，当然也就无法返回响应数据了

> **在这里我们就需要运用到编程语言的回调机制来解决了**

* 首先我们需要定义一个接口

```kotlin
interface HttpCallbackListener {
    fun onFinish(response:String)
    fun onError(e:Exception)
}
```

> response:String 代表服务器返回的数据
>
> e:Exception 记录错误的详细信息

* 接着修改我们HttpUtil中的代码

```kotlin
object HttpUtil {
    fun sendHttpRequest(address: String,listener: HttpCallbackListener){

        thread {
            var connection: HttpURLConnection?=null
            try {
                val response = StringBuilder()
                val url = URL(address)
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout=8000
                connection.readTimeout=8000
                val input = connection.inputStream
                //下面对获取到的输入流进行读取
                val reader = BufferedReader(InputStreamReader(input))
                reader.use {
                    reader.forEachLine {
                        response.append(it)

                    }
                }
                //回调onFinish()方法
                listener.onFinish(response.toString())
            }catch (e: Exception) {
                e.printStackTrace()
                //回调onError()方法
                listener.onError(e)
            }finally {
                connection?.disconnect()
            }
        }
    }
}
```

> 1. 我们多了一个HttpCallbackListener的参数，并在方法的内部开启了一个子线程
> 2. 然后在子线程当中执行具体的网络操作
>    * 注意：==子线程是无法通过return语句返回数据的==，因此我们将服务器响应的数据传入onFinish()方法
>    * 异常传入onError()方法
> 3. 现在两个参数当中，在调用的时候，还需要传入一个HttpCallbackListener**的实例**

* 调用如下（记得传入一个实例）

```kotlin
HttpUtil.sendHttpRequest(address,object : HttpCallbackListener{
            override fun onFinish(response:String){
                //得到服务器返回的内容
            }
            override fun onError(e:Exception){
                //对异常情况进行处理
            }
        })
```

> 当服务器成功响应的时候，就可以在onFinish方法中响应数据进行处理了

> 我们看到，使用HttpURLConnection好像挺麻烦的，那么OkHttp就会非常简单了

```kotlin
object HttpUtil {
    fun sendOkHttpRequest(address: String,callback:okhttp3.Callback){
        val client = OkHttpClient()
        val request = Request.Builder().url(address).build()
        client.newCall(request).enqueue(callback)
    }
}
```

> 可以看到，这里没有像以前一样使用execute()，而是调用enqueue方法，并把okhttp3.Callback参数传入，相信你可以看出来了，**exqueue内部已经帮我们开好子线程了，然后会在子线程中执行HTTP请求，并将最终的请求结果回调到okhttp3.Callback当中**

> 调用如下：

```kotlin
HttpUtil.sendOkHttpRequest("https://www.baidu.com", object : Callback {
    override fun onFailure(call: Call, e: IOException) {
        //对异常进行处理
    }

    override fun onResponse(call: Call, response: Response) {
        //得到服务器返回的具体内容
        val responseData = response.body?.string()
    }
})
```

> 记住，无论你用哪个，==最终的回调接口都还是在子线程中运行的==，不能进行UI操作，除非记住runOnUiThread()方法进行线程转换

## Retrofit（最好用的网络库）

> 同样是Square公司开发的网络库，但它的定位于OkHttp完全不同。OkHttp侧重于底层的通信实现，Retrofit侧重于上层接口的封装，使得我们可以用面向对象的思维进行网络操作。详细你可以见它的git护不住也
>
> * 我们新建一个RetrofitTest项目来试一下吧

### 基本用法

> 首先我们先来谈一下，Retrofit的基本设计思想

它的设计是基于以下几个事实的。

1. 同一款应用程序中所发起的网络请求绝大多数指向的是同一个服务器域名。这个很好理解，因为任何公司的客户端和服务器都是配套的，很难想象一个客户端去多个服务器获取不同的数据
2. 服务器提供的接口通常是可以根据功能来柜内的。
   * ==新增用户，修改，查询可以归成一类==
   * ==上架书本，销售书本，查询可供销售的书本可以归成一类==
3. 最后，开发者肯定更加习惯于调用一个接口，获取它的返回值的编码方式，担当调用的是服务器的接口时候，很难想象想象该如何使用这样的编码方式

> Retrofit就是基于以上的事实来进行设计的

1. 首先我们可以配置好一个根路径，然后在指定服务器接口地址时只需要使用相对路径即可，这样就不用每次都指定完整的URL地址了
2. 允许我们对服务器接口进行归类，将功能同属一类的接口定义到同一个接口文件当中，从而使得代码更加合理
3. 我们完全不关心网络通信的细节，只需要在接口文件中声明一些列方法和返回值，然后通过注解的方式制定该方法对应哪个服务器接口以及需要的参数
   * 当我们程序调用该方法的时候，Retrofit会自动的向对应服务器接口发起请求，并将响应的数据解析成返回值声明的类型，这使得我们可以用面向对象的思维来进行操作



> 下面，我们快速体验一下吧

* dependencies闭包中添加下面内容：

```xml
implementation 'com.squareup.retrofit2:retrofit:2.6.1'
implementation 'com.squareup.retrofit2:converter-gson:2.6.1'
```

> 上面第一条依赖会将OkHttp，Okio这几个库都一起下载下来
>
> 因为是Android开发，理所当然使用GSON

* 我们继续使用书本上JSON当中的数据接口，由于Retrofit会借助GSON将JSON数据转换成对象，因此在这里同样需要新增一个App类，加入属性字段

```kotlin
class App(val id: String,val name:String,val version:String) 
```

* 接下来，我们可以根据服务器接口的功能进行归类，创建不同种类的接口文件，并在其中定义对应具体服务器接口方法。不过我们之前Apache服务器上面其实只有一个获取JSON数据的接口(书本上JSON部分,这里没讲解了，可回去看书)，因此这里只需要定义一个接口文件，包含一个方法即可：

```kotlin
interface AppService {
    @GET("get_data.json")
    fun getAppData():Call<List<App>>
}
```

> 通常命名都是具体功能种类名开头+Service结尾

> * 发起一条GET请求，请求地址就是我们在@GET注释中传入的具体参数。注意这里只需要传入请求地址的相对路径即可，**根路径我们会在后面补上！**
> * getAppData方法返回值必须声明为Call类型，并通过泛型来制定服务器响应的数据应该转换成什么对象。由于服务器响应的是一个包含App数据的JSON数，因此这里我们将泛型声明为List<App>。
> * 当然Retrofit还提供了强大的Call Adapters功能来语序我们自定义方法返回值的类型，但暂时这个不在讨论范围之类

* activity_main.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Get App Data"
        android:textAllCaps="false"
        android:id="@+id/getAppData"/>

</LinearLayout>
```

* MainActivity

```kotlin
package com.workaholiclab.retrofittest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private val mkey ="MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getAppData.setOnClickListener { 
            val retrofit = Retrofit.Builder().baseUrl("http://10.0.2.2/").addConverterFactory(GsonConverterFactory.create()).build()
            val appService = retrofit.create(AppService::class.java)
            appService.getAppData().enqueue(object : Callback<List<App>>{
                override fun onFailure(call: Call<List<App>>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<List<App>>, response: Response<List<App>>) {
                    val list =response.body()
                    if(list!=null){
                        for (app in list){
                            Log.d(mkey,"id is ${app.id}")
                            Log.d(mkey,"name is ${app.name}")
                            Log.d(mkey,"version is ${app.version}")
                        }
                    }
                }

            })
        }
    }
}
```

* 最后别忘了加上network_config.xml文件和注册文件！

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.workaholiclab.retrofittest">

    <uses-permission android:name="android.permission.INTERNET"/>
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme"
        android:networkSecurityConfig="@xml/network_config">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```





### 处理复杂的接口地址类型

> 上一小节，我们通过实例程序向一个非常简单的服务器接口地址发送请求:http:/10.0.2.2/get_data.jason.
>
> 然而在真实开发的过程当中，我们服务器提供的接口地址不可能一直那么简单，可能是千变万化的，下面来看看Retrofit如何来解决吧



* 先定义一个Data类

```kotlin
class Data(val id:String,val name:String, val version:String)
```

* 传入不同的page，GET http:/10.0.2.2/<page>/get_data.json.

```kotlin
interface ExampleService {
    @GET("{page}/get_data.json")
    fun getData(@Path("page")page:Int): Call<Data>
}
```

* http://10.0.2.2/get_data.jason?u=<user>&t=<token>

> 多个参数用&分割开来

```kotlin
@GET("get_data.json")
fun getData2(@Query("u")user:String,@Query("t")token:String):Call<Data>
```

* HTTP不单只有GET请求，还有POST（提交），PUT & PATCH（修改服务器数据），DELETE}（删除）



* Delete http://example.com/data/<id>

```kotlin
@DELETE("data/{id}")
fun deleteData(@Path("id")id:String):Call<ResponseBody>
```

> 为什么泛型指定为ResponseBody，其实除了GET这种方法，其他方法对服务器上的数据都不关心，用ResponseBody表示可以接收任意类型的响应数据，并且不会对响应数据进行解析

* POST一条数据上去

  > http://example.com/data/create

```kotlin
@POST("data/create")
fun postData(@Body data:Data):Call<ResponseBody>
```

> Retrofit会自动将Data对象自动转化成为JSON数据，并放到HTTP的body部分

* 有些服务器接口还要求我们在HTTP请求的header中指定参数

> GET http://example.com/get_data.json
>
> User-Agent: okhttp
>
> Cache-Control: max-age=0

* 静态指定header值写法

```kotlin
@Headers("User-Agent:okhttp","Cache-Control:max-age=0")
@GET("get_data.json")
fun getData3():Call<Data>
```

* 动态指定header值写法

```kotlin
@GET("get_data.json")
fun getData4(@Header("User-Agent")userAgent:String,
             @Header("Cache-Control")cacheControl:String):Call<Data>
```



### Retrofit构建器的最佳写法

> 我们之前获取Service的接口的动态代理对象实在是太麻烦了
>
> * 之前写法如下面所示：

```kotlin
Retrofit.Builder().baseUrl("http://10.0.2.2/")
				.addConverterFactory(GsonConverterFactory.create()).build()
val appService = retrofit.create(AppService::class.java)
```

> 由于Retrofit对象是全局通用的，只需要在调用create方法是针对不同的Service接口传入响应的Class类型即可，因此我们把这一部分的功能封装起来



```kotlin
object ServiceCreator {
    private const val BASE_URL="http://10.0.2.2/"
    
    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    
    fun <T> create(serviceClass: Class<T>):T= retrofit.create(serviceClass)
}
```

> **==上面代码仍然有优化空间，我们采用泛型实化来优化==**,
>
> [泛型实化]: https://blog.csdn.net/baidu_39589150/article/details/112232682
>
> 

* 优化：

```kotlin
object ServiceCreator {
    private const val BASE_URL="http://10.0.2.2/"

    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

    fun <T> create(serviceClass:Class<T>):T = retrofit.create(serviceClass)
    
    inline fun <reified T>create():T= create(T::class.java)
}
```

* 调用

```kotlin
val appService = ServiceCreator.create(AppService::class.java)
```



## 协程编写高效并发文件

> **==详见课本p461页！！！,这里一定一定要回去看，是对上面写法的优化，简化回调的代码！！！==**



> 网络技术博客完更

# JetPack

> 2021.3.4 
>
> Gary哥哥的哥哥

> 高级程序开发组件
>
> 来到这里其实已经可以自己独立开发Android应用程序了，但开发的好不好，重点是代码的质量优越，项目框架是否合理

## 简介

> Jetpack是一个开发组件工具集，它的主要目的是帮助我们编写出更加简洁的代码，并且简化我们的开发过程。
>
> * 它的一个特点是，大部分组件不依赖于Android系统的版本，有着很好的向下兼容性
>   * 下面来看看Jetpack的全家福：

![img](https://img-blog.csdnimg.cn/20200708224514617.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0NTRE5fRGFTaHVpTml1,size_16,color_FFFFFF,t_70)

> 事实上，我们不可能对Jetpack的每一个组件都深入学习，这是一个大工程，我们需要关注的其实还是架构组件，目前Android官方最为推荐的项目架构就是MVVM，因此Jetpack中的许多架构组件是专门为MVVM架构量身打造的
>
> * MVVM架构将会在以后的实战当中讲解

## ViewModel

> 最重要的组件之一，Activity的任务繁重，在大项目中会变得非常臃肿
>
> * 帮助Activity分担一部分工作
> * **专门用于存放与界面相关的数据**
>   * 界面上看到的数据，它的相关变量都应该存放在ViewModel中，而不是Activity
> * 旋转屏幕时候，不会重新创建

> 生命周期示意图：

![img](https://upload-images.jianshu.io/upload_images/13883177-226ad143b0dfec89.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/522)

### 基本用法

> 下面我们通过一个简单计数器的例子来简单看一下它的用法吧

* 添加依赖文件

```xml
implementation 'androidx.lifecycle:lifecycle-extensions:2.1.0'
```

* 为MainActivity创建一个对应的MainViewModel

```kotlin
class MainViewModel:ViewModel() {
    val counter = 0
}
```



* activity_main.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <TextView
        android:id="@+id/infoText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:textSize="32sp"/>
    
    <Button
        android:id="@+id/plusOneBtn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:text="Plus One"/>

</LinearLayout>
```

* 实现

> 注意两次refresh

```kotlin
class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel=ViewModelProviders.of(this).get(MainViewModel::class.java)
        plusOneBtn.setOnClickListener { 
            viewModel.counter++
            refreshCounter()
        }
        refreshCounter()
    }

    private fun refreshCounter() {
        infoText.text=viewModel.counter.toString()
    }
}
```

> ```ViewProviders.of(<你的Activity或Fragment实例>).get(<你的ViewModel>::class.java)```
>
> ViewModel的生命周期长于Activity，因此不要去创建ViewModel实例，用上面的方法获取实例

### 传递参数

> 借助ViewModelProvider.Factory就可以向ViewModel的构造函数传递参数了

* 修改MainViewModel

```kotlin
class MainViewModel(countReserved:Int):ViewModel() {
    var counter = countReserved
}
```

* 新建MainViewModelFactory

```kotlin
class MainViewModelFactory(private val countReserved:Int):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(countReserved) as T
    }
}
```



* 修改xml文件
  * 新增清零键

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <TextView
        android:id="@+id/infoText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:textSize="32sp"/>

    <Button
        android:id="@+id/plusOneBtn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:text="Plus One"/>
    
    <Button
        android:id="@+id/clearBtn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:text="Clear"/>
        

</LinearLayout>
```

* MainActivity

```kotlin
class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    lateinit var sp:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp=getPreferences(Context.MODE_PRIVATE)
        val countReserved = sp.getInt("count_reserved",0)
        setContentView(R.layout.activity_main)
        viewModel=ViewModelProviders.of(this,MainViewModelFactory(countReserved)).get(MainViewModel::class.java)
        plusOneBtn.setOnClickListener {
            viewModel.counter++
            refreshCounter()
        }
        refreshCounter()
        clearBtn.setOnClickListener {
            viewModel.counter=0
            refreshCounter()
        }
    }

    override fun onPause() {
        super.onPause()
        val edit = sp.edit()
        edit.putInt("count_reserved",viewModel.counter)
        edit.commit()
    }

    private fun refreshCounter() {
        infoText.text=viewModel.counter.toString()
    }
}
```

> ``` viewModel=ViewModelProviders.of(this,MainViewModelFactory(countReserved)).get(MainViewModel::class.java)```
>
> * 获取SharedPreference的实例``` val countReserved = sp.getInt("count_reserved",0)```,没有则为0
> * onPause方法对当前计数进行保存，保证程序不管退出还是进入后台，数据都不会丢失
>   * 只有点击ClearBtn还会清零



## LifeCycle

> 某个界面发起一条网络请求，但是当请求得到响应的时候，界面或许已经关闭，这个时候就不应该继续对响应的结果进行处理了，因此我们需要感知Activity的生命周期
>
> * 这里我们重点看看如何在一个非Activity中感知Activity的生命周期
>   * 可以用隐藏Fragment或者手写监听器来感知



> **下面通过手写监听器来进行感知**

* 让MyObserver感知Activity的生命周期

```kotlin
class MyObserver {
    fun activityStart(){
        
    }
    
    fun activityStop(){
        
    }
}
```

```kotlin
class MainActivity : AppCompatActivity() {
    lateinit var observer: MyObserver
    override fun onCreate(savedInstanceState: Bundle?) {
        observer = MyObserver()
    }

    override fun onStart() {
        super.onStart()
        observer.activityStart()
    }

    override fun onStop() {
        super.onStop()
        observer.activityStop()
    }
```

> **==这种方法可以是可以，但写起来不是很优雅，需要在Activity中编写大量的逻辑处理==**

> **下面我们利用Lifecycles组件来实现**

```kotlin
class MyObserver : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun activityStart(){
        Log.d("MyObserver","activityStart")
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun activityStop(){
        Log.d("MyObserver","activityStop")
    }
}
```

> @OnLifecycleEvent注释，传入一种生命周期事件
>
> **但是，此时代码仍然无法正常运作，Activity生命周期发生变化的时候，并没有人去通知MyObserver，但我们不想像之前一样在Activity中一个一个手动实现**

* 使用LifecycleOwner

> 使得MyObserver得到通知

```kotlin
lifecycleOwner.lifecycle.addObserver(MyObserver())
```

> 首先调用getLifecycleOwner获取对象，addObserver观察LifeCycleOwner的生命周期，再把MyObserver()实例穿进去即可

* MainActivity大大简化

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycle.addObserver(MyObserver())
    }

}
```

> 不过现在MyObserver虽能感知Activity的生命周期变化，却没有办法主动获取当前的生命周期状态。
>
> 只需要在MyObserver构造函数中将LifeCycle对象传进来即可

```kotlin
class MyObserver(val lifecycle: Lifecycle) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun activityStart(){
        Log.d("MyObserver","activityStart")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun activityStop(){
        Log.d("MyObserver","activityStop")
    }
}
```

我们可以再任何地方调用lifecycle.currentState来主动获取当前的生命周期状态

* 测试

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycle.addObserver(MyObserver(lifecycle))
        println(lifecycle.currentState.toString())
    }
}
```



## LiveData

> LiveData是JetPack提供的一个响应式编程组件，它可以包含任何类型的数据，并在数据发生变化的时候通知给观察者。
>
> * LiveData一般与ViewModel结合起来使用 

### 基本用法

> 我们之前编写的那个计数器功能非常简单，但其实是存在问题的
>
> 如果在ViewModel里面去开启了线程去执行一些耗时的逻辑，那么在点击按钮之后就立即去获取最新的数据，得到的肯定还是原来的数据

> 我们会发现，我们一直都是使用Activity中手动获取ViewModel中的数据这种交互方式，但是ViewModel却无法将数据的变化主动通知给Activity

> **这里我们将计数器的计数使用LiveData来包装，然后在Activity中观察他就可以主动将数据变化通知Activity了**
>
> **特别提醒，千万不要把Activity的实例传给ViewModel，因为后者生命周期是长于前者的！！！**

> 我们修改我们之前的项目代码

* MainViewModel

```kotlin
class MainViewModel(countReserved:Int):ViewModel() {
    var counter = MutableLiveData<Int>()
    init {
        counter.value=countReserved
    }
    fun plusOne(){
        val count = counter.value?:0
        counter.value=count+1
    }
    
    fun clear(){
        counter.value=0
    }
}
```

> var counter = MutableLiveData<Int>() 它的用法简单，主要是getValue，setValue，postValue
>
> 分别为获取LiveData数据；设置LiveData数据但是只能在主线程中调用；postValue用于在非主线程中给LiveData设置数据。
>
> 上述代码就是对应getValue和setValue的语法糖写法

* MainActivity

```kotlin
class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    lateinit var sp:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp=getPreferences(Context.MODE_PRIVATE)
        val countReserved = sp.getInt("count_reserved",0)
        setContentView(R.layout.activity_main)
        viewModel=ViewModelProviders.of(this,MainViewModelFactory(countReserved)).get(MainViewModel::class.java)
        plusOneBtn.setOnClickListener {
            viewModel.plusOne()
        }
        refreshCounter()
        clearBtn.setOnClickListener {
            viewModel.clear()
        }
        viewModel.counter.observe(this, Observer { count->infoText.text=count.toString() })
    }

    override fun onPause() {
        super.onPause()
        val edit = sp.edit()
        edit.putInt("count_reserved",viewModel.counter.value?:0)
        edit.commit()
    }

    private fun refreshCounter() {
        infoText.text=viewModel.counter.toString()
    }
}
```

> 核心：
>
> ```kotlin
> viewModel.plusOne()
> viewModel.clear()
> edit.putInt("count_reserved",viewModel.counter.value?:0)
> 
> //observe方法来观测数据变化
> viewModel.counter.observe(this, Observer { count->infoText.text=count.toString() })
> //p1：LifecycleOwner对象，Activity本身就是，因此传入this；p2：Oberserver接口，当counter数据变化时会回调到这里，因此我们在这里将最新的计数更新到界面上即可
> ```



这里说明一下，2019年的Google开发者大会上，Android团队官宣了Kotlin First，并承诺未来会在JetPack中提供更多专门面向Kotlin语言的API，这里举一个小例子:

```xml
implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.2.0'
```

> 然后我们就可以这样写了：

```kotlin
viewModel.counter.observe(this){count->infoText.text=count.toString()}
```

> 与原来```viewModel.counter.observe(this, Observer { count->infoText.text=count.toString() })```写法是等价的



> **虽然前面的讲解是正确的，但是这样子操作会把counter这个可变的LiveData暴露给了外部**，**破坏了封装性**，下面我们来看看如何改进

* MainViewModel

  > 保证在非ViewModel中只能观察LiveData的数据变化，而不能给LiveData设置数据
  >
  > * 改进写法

  ```kotlin
  class MainViewModel(countReserved:Int):ViewModel() {
      val counter:LiveData<Int>
      get() = _counter
      private val _counter = MutableLiveData<Int>()
      init {
          _counter.value=countReserved
      }
      fun plusOne(){
          val count = _counter.value?:0
          _counter.value=count+1
      }
  
      fun clear(){
          _counter.value=0
      }
  }
  ```

### map和switchMap

> 上面介绍的LiveData的基本用法可以满足大部分的开发需求了，但是当前项目变得复杂之后，可能会出现一些更加特殊的需求，这里就提供两种转换方法

#### map

> 实际包含数据的LiveData和仅用于观察数据的LiveData进行转换，下面举一个例子

```kotlin
data class User(var firstName:String,var lastName:String,var age :Int)
```



```kotlin
class MainViewModel(countReserved:Int):ViewModel() {
    private val userLiveData = MutableLiveData<User>()
    val userName:LiveData<String> = Transformations.map(userLiveData){user->
        "${user.firstName} ${user.lastName}"
    }
    ......
}
```

> ```kotlin
> val userName:LiveData<String> = Transformations.map(userLiveData){user->
>      "${user.firstName} ${user.lastName}"
>  }
> ```
>
> p1:原始LiveData对象，p2：转换函数
>
> * 这里转换逻辑很简单，就是把user转换成为转换成为只包含firstName和lastName的字符串
> * 然后再讲转换之后的数据通知userName的观察者

#### switchMap

> **使用场景非常固定，但是可能比前者更加常用**

> **我们前面的LiveData对象都是在ViewModel中创建的，但是实际上不会那么理想，很可能ViewModel中的某个LiveData对象是调用另外的方法获取的**

> 下面我们来看一下例子

```kotlin
object Repository {
    fun getUser(userId:String):LiveData<User>{
        val liveData = MutableLiveData<User>()
        liveData.value=User(userId,userId,0)
        return liveData
    } 
}
```

> 上面模拟创建一个User对象

```kotlin
private val userLiveData = MutableLiveData<User>()
    val userName:LiveData<String> = Transformations.map(userLiveData){user->
        "${user.firstName} ${user.lastName}"
    }
    
    private val userIdLiveData=MutableLiveData<String>()
    val user:LiveData<User> = Transformations.switchMap(userIdLiveData){
        userId->
        Repository.getUser(userId)
    }
    
    fun getUser(userId:String){
        userIdLiveData.value=userId
    }
......
}
```

> ```kotlin
> val user:LiveData<User> = Transformations.switchMap(userIdLiveData){
>      userId->
>      Repository.getUser(userId)
>  }
> ```
>
> ==**工作原理就是将转换函数中返回的LiveData对象转化成为另一个可观察的LiveData对象**==

* MainActivity中观察

```kotlin
getUserBtn.setOnClickListener { 
    val userId =(0..10000).random().toString()
    viewModel.getUser(userId)
}
viewModel.user.observe(this, Observer { user->infoText.text=user.firstName })
```



> 来到这里或许大家会有些疑惑，ViewModel中获取数据的方法有可能是没有参数的，这时候应该怎么写呢？
>
> 在没有可观察数据的情况下，我们需要创建一个空的LiveData对象，如下所示：

```kotlin
class MyViewModel:ViewModel() {
    private val refreshLiveData = MutableLiveData<Any?>()
    val refreshResult = Transformations.switchMap(refreshLiveData){
        Repository.refresh()
    }

    fun refresh(){
        //触发数据变化，（只要调用setValue或者postValue数据就会变化）
        refreshLiveData.value = refreshLiveData.value
    }
}
```



> LiveData作为Activity和ViewModel的桥梁，并且不会有内存泄露的风险，依靠的就是Lifecycles组件，LiveData内部就是使用Lifecycles组件来自我感知生命周期，从而及时释放，避免内存泄漏



## Room

> 前面我们学习过SQLite数据库的使用方法，这些原生CRUD的API虽然简单易用，但是放到大型项目当中的话，会非常容易让项目的代码变得环路安，除非进行很好的封装。市面上出现了诸多专门为Android数据库设计的ORM框架（关系对象映射）

> ORM优点：
>
> * 面向对象的思维和数据库交互，绝大多数情况下不再和SQL语句打交道
> * 项目整理逻辑不再显得混乱

### CRUD

> Room三部分：
>
> * Entity实体类，每个实体类在数据库中有一张对应的表，并且表中的列是根据实体类中的字段自动生成的
> * Dao。数据访问对象，通常会在这里对数据库的各项操作进行封装。实际开发过程中，逻辑层不需要和底层数据库交互了，直接和Dao层进行交互即可。
> * Database。用于定义数据库中的关键信息，包括数据库的版本号，以及那些实体类，和提供Dao层的访问实例

> 我们继续在前面项目的基础上进行改造

```kotlin
apply plugin: 'kotlin-kapt'

implementation 'androidx.room:room-runtime:2.1.0'
kapt 'androidx.room:room-compiler:2.1.0'
```

* Entity实体类

```kotlin
@Entity
data class User(var firstName:String,var lastName:String,var age :Int){
    //主键自动生成
    @PrimaryKey(autoGenerate = true)
    var id:Long=0
}
```

* Dao

> 这部分是Room用法最关键的地方，因为所有访问数据库的操作都是在这里封装的

1. 实现一个UserDao接口，注意必须使用接口（这一点和Retrofit是类似的）

```kotlin
@Dao
interface UserDao {
    @Insert
    fun insertUser(user:User):Long
    
    @Update
    fun updateUser(newUser:User)
    
    @Query("select * from User")
    fun loadAllUsers():List<User>
    
    @Query("select * from User where age > :age")
    fun loadUsersOlderThan(age:Int):List<User>
    
    @Delete
    fun deleteUser(user:User)
    
    @Query("delete from User where lastName = :lastName")
    fun deleteUserByLastName(lastName:String):Int
}
```

> **虽然仍然要写SQL，但是Room是支持编译时候自动检查SQL语法的，减轻我们查找错误的负担**



* Database

> 这一部分的写法是非常固定的，只需要定义好三个部分的内容：
>
> 数据库的版本号，包含哪些实体类，以及提供Dao层的访问实例

> 新建一个AppDatabase.kt文件

```kotlin
@Database(version = 1,entities = [User::class])//若是多个实体类之间用逗号分开
abstract class AppDatabase:RoomDatabase(){
    abstract fun userDao():UserDao//具体方法实现是有Room在底层自动完成的
    companion object{
        private var instance:AppDatabase?=null
        
        @Synchronized
        fun getDatabase(context: Context):AppDatabase{
            instance?.let { 
                return it
            }
            //没有，则构建，如下：
            return Room.databaseBuilder(context.applicationContext,AppDatabase::class.java,"app_database")
            //p1:一定要用applicationContext（避免内存现楼）
            //p2：AppDatabase的Class类型
            //p3:数据库名
            .build().apply { 
                instance=this
            }//完成构建
        }
    }
}
```



* 下面我们在activity_main.xml中增加增删改查的按钮

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <TextView
        android:id="@+id/infoText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:textSize="32sp"/>

    <Button
        android:id="@+id/plusOneBtn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:text="Plus One"/>
    

    <Button
        android:id="@+id/clearBtn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:text="Clear"/>

    <Button
        android:id="@+id/getUserBtn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="get User"
        android:layout_gravity="center_horizontal"/>
    
    <Button
        android:id="@+id/addDataBtn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Add Data"
        android:layout_gravity="center_horizontal"/>
    <Button
        android:id="@+id/updateDataBtn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Update Data"
        android:layout_gravity="center_horizontal"/>
    <Button
        android:id="@+id/deleteDataBtn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Delete Data"
        android:layout_gravity="center_horizontal"/>

    <Button
        android:id="@+id/queryDataBtn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Query Data"
        android:layout_gravity="center_horizontal"/>


</LinearLayout>
```

* MainActivity

```kotlin
class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    lateinit var sp:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp=getPreferences(Context.MODE_PRIVATE)
        val countReserved = sp.getInt("count_reserved",0)
        setContentView(R.layout.activity_main)
        viewModel=ViewModelProviders.of(this,MainViewModelFactory(countReserved)).get(MainViewModel::class.java)
        plusOneBtn.setOnClickListener {
            viewModel.plusOne()
        }
        refreshCounter()
        clearBtn.setOnClickListener {
            viewModel.clear()
        }
        viewModel.counter.observe(this, Observer { count->infoText.text=count.toString() })

        getUserBtn.setOnClickListener {
            val userId =(0..10000).random().toString()
            viewModel.getUser(userId)
        }
        viewModel.user.observe(this, Observer { user->infoText.text=user.firstName })

        //数据库CRUD
        val userDao =AppDatabase.getDatabase(this).userDao()
        val user1=User("Tom","James",40)
        val user2=User("Tom","Jimmy",63)
        addDataBtn.setOnClickListener {
            thread{
                user1.id=userDao.insertUser(user1)
                user2.id=userDao.insertUser(user2)
            }
        }
        updateDataBtn.setOnClickListener {
            thread {
                user1.age=42
                userDao.updateUser(user1)
            }
        }
        deleteDataBtn.setOnClickListener { 
            thread { 
                userDao.deleteUserByLastName("Hanks")
            }
        }
        queryDataBtn.setOnClickListener { 
            thread { 
                for(user in userDao.loadAllUsers()){
                    Log.d("MainActivity",user.toString())
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val edit = sp.edit()
        edit.putInt("count_reserved",viewModel.counter.value?:0)
        edit.commit()
    }

    private fun refreshCounter() {
        infoText.text=viewModel.counter.toString()
    }
}
```

## Room的数据库升级

> Room在数据库升级方面的设计非常繁琐，比起原生的SQLiteDatabase简单不到那里去，每一次都需要手动编写升级逻辑才行。
>
> * 为此guolin大神编写了一个数据库框架LitePal则可以根据实体类的变化自动升级数据库，如果感兴趣可以自行去搜索



* 不过，如果处于开发的测试剪短，不想编写那么麻烦的升级逻辑，Room到可以提供了一个简单粗暴的方法:

```kotlin
Room.databaseBuilder(context.applicationContext,AppDatabase::class.java,"app_database")
.fallbackToDestructiveMigration()
.build()
```

> Room会将当前数据库销毁，重新创建，随之副作用就是数据会全部丢失

> 但产品上线了，上面的方法就万万不可了，我们还是老老实实学习一下Room中升级数据库的正确写法吧

> 这一部分的讲解内容为了节省空间就嵌入到代码的注释当中讲解

* 前面两步和上面讲解的一致

```kotlin
@Entity
data class Book(var name:String,var pages:Int) {
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
}
```



```kotlin
@Dao
interface BookDao {
    @Insert
    fun insertBook(book:Book):Long
    
    @Query("select * from Book")
    fun loadAllBooks():List<Book>
}
```



* 下面修改AppDatabase中的代码

```kotlin
//version改成2，两个实体类用逗号分割开来
@Database(version = 2,entities = [User::class,Book::class])
abstract class AppDatabase:RoomDatabase(){
    abstract fun userDao():UserDao
    abstract fun bookDao():BookDao

    companion object{
        //Migration匿名类,传入两个参数表示当前数据库从版本1升级到版本2的时候执行匿名类当中的升级逻辑
        val MIGRATION_1_2 = object  :Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("create table Book (id integer primary key autoincrement not null, name text not null,pages integer not null)")
            }

        }

        private var instance:AppDatabase?=null

        @Synchronized
        fun getDatabase(context: Context):AppDatabase{
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,AppDatabase::class.java,"app_database")
                //升级数据库逻辑
                .addMigrations(MIGRATION_1_2)
                .build().apply {
                instance=this
            }
        }
    }
}
```



* 下面我们继续升级数据库，增多一个作者属性

```kotlin
@Entity
data class Book(var name:String,var pages:Int,var author:String) {
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
}
```



```kotlin
//version改成3，两个实体类用逗号分割开来
@Database(version = 3,entities = [User::class,Book::class])
abstract class AppDatabase:RoomDatabase(){
    abstract fun userDao():UserDao
    abstract fun bookDao():BookDao

    companion object{
        //Migration匿名类,传入两个参数表示当前数据库从版本1升级到版本2的时候执行匿名类当中的升级逻辑
        val MIGRATION_1_2 = object  :Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("create table Book (id integer primary key autoincrement not null, name text not null,pages integer not null)")
            }
        }
        
        val MIGRATION_2_3 = object :Migration(2,3){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table Book add column author text not null default 'unknown'")
            }

        }

        private var instance:AppDatabase?=null

        @Synchronized
        fun getDatabase(context: Context):AppDatabase{
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,AppDatabase::class.java,"app_database")
                //升级数据库逻辑
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build().apply {
                instance=this
            }
        }
    }
}
```



## WorkManager

> Android后台机制是一个很复杂的话题，后台Service的API也是频繁变更的，到底如何编写后台管理的代码才能保证应用程序在不同Android版本下的兼容性呢？
>
> 未解决这个问题，Android推出了WorkManager组件，**非常适用于处理一些要求定时完成的任务**
>
> 它还支持周期性任务，链式任务等功能
>
> * 不过，需要明确的是，它和Service并不相同，实际上没有什么直接的联系，WorkManager知识一个定时任务工具，它可以保证及时应用退出甚至手机重启的情况下，之前注册的任务仍然可以得到执行，而Service是Android的四大组件之一，它在没有被销毁的情况下是一致保持在后台运行的
>   * 另外，WorkManager注册的周期性任务不一定保证准时执行，只不是bug，而是系统为了减少电量消耗，可能会将出发时间临近的任务放在一起执行，可以大幅度减少CPU被唤醒的次数，延长电池使用寿命

> 下面一起来看一下WorkManager的具体用法吧

### 基本用法

> 首先导入依赖文件

```xml
implementation 'androidx.work:work-runtime:2.2.0'
```

> 导入依赖过后，其实WorkManager非常简单，一共分为三步

1. 定义一个后台任务，并且实现具体逻辑
2. 配置该后台任务的运行条件和约束信息，并构建后台任务请求
3. 将该后台任务请求传入WorkManager的enqueue()方法中，系统会在合适的时间运行

> 下面我们根据上面的步骤一步一步的来实现：

1. 

```kotlin
class SimpleWorker(context: Context,params:WorkerParameters):Worker(context,params) {
    override fun doWork(): Result {
        Log.d("SimpleWorker","do work in SimpleWorker")
        return Result.success()
    }
}
```

> 第一步后台任务的写法是非常固定的，也非常好理解。首先每一个后台任务都必须继承自Worker类，并且调用它唯一的构造函数。然后重写父类的doWork()方法，这个方法中编写具体的后台任务逻辑即可

> 需要注意的是：doWork()方法不会运行在主线程当中，**因此可以放心地在这里执行耗时逻辑**，不过这里简单起见知识打印了一行日志
>
> * doWork方法返回值返回任务结束还是失败结果，还有一个Result.retry(),其实也是代表失败，知识可以结合WorkRequest.Builder的setBackoffCriteria()方法来重新执行任务

> 就是那么简单，下面来看一下第二步：

2. 其实这一步是最复杂的，因为可以配置的内容非常得多，不过目前我们还只是学习WorkManager的基本用法，因此只进行最基本的配置就可以了。

* OneTimeWorkRequest.Builder是WorkRequest.Build的子类，用于构建单次运行的后台任务请求

```kotlin
val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java).build()
```

* WorkRequest.Build还有另外一个子类，用于构建周期性运行的后台请求

```kotlin
val request1 = PeriodicWorkRequest.Builder(SimpleWorker::class.java,15,TimeUnit.MINUTES).build()
```

> 也很容易看懂，就不再解释了

3. 最后一步，将构建出的后台任务请求传入WorkManager的enqueue()方法中，系统会在合适的时间来运行

```kotlin
WorkManager.getInstance(context).enqueue(request)
```

> 下面我们通过具体例子来看看，继续修改我们之前的项目：

* activity_main.xml增多一个Button

```xml
<Button
    android:id="@+id/doWorkBtn"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Do Work"
    android:layout_gravity="center_horizontal"/>
```



* MainActivity增多监听事件

```kotlin
doWorkBtn.setOnClickListener { 
    val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java).build()
    WorkManager.getInstance(this).enqueue(request)
}
```



### 处理复杂任务

> 除了控制时间之外，实际上WorkMange还允许我们控制其他许多方面的东西

* 让后台任务延迟进行

```kotlin
val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java).setInitialDelay(5,TimeUnit.MINUTES).build()
```

* 给后台任务添加一个标签

```kotlin
val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java).setInitialDelay(5,TimeUnit.MINUTES).addTag("simple").build()
```

> 这样子我们可以通过标签来取消后台任务请求：

```kotlin
WorkManager.getInstance(this).cancelAllWorkByTag("simple")
```

> 当然没有标签也可以使用id
>
> * 但这只能取消一个后台任务请求

```kotlin
WorkManager.getInstance(this).cancelWorkById(request.id)
```

> 取消所有后台任务请求

```kotlin
WorkManager.getInstance(this).cancelAllWork()
```

* 后台doWork()方法返回Result.retey()，可以用下面方法重新执行任务

```kotlin
val request1 = OneTimeWorkRequest.Builder(SimpleWorker::class.java).setInitialDelay(5,TimeUnit.MINUTES)val request1 = OneTimeWorkRequest.Builder(SimpleWorker::class.java).setInitialDelay(5,TimeUnit.MINUTES).setBackoffCriteria(BackoffPolicy.LINEAR,10,TimeUnit.MINUTES).build().build()
```

> 核心：```setBackoffCriteria(BackoffPolicy.LINEAR,10,TimeUnit.MINUTES)```
>
> * p1：用于指定如果任务失败再次致信失败，下次重试的时间应该以什么样的延迟方式



* 之前doWork()方法返回的success()和failure()，实际上就是用于通知任务运行结果的，我们用下面的代码来进行**监听**

```kotlin
WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.id).observe(this, Observer {workInfo->
    if(workInfo.state==WorkInfo.State.SUCCEEDED){
        Log.d("MainActivity","do work succeeded")
    }else if(workInfo.state==WorkInfo.State.FAILED){
        Log.d("MainActivity","do work failed")
    }
})
```

> 另外你还可以用getWorkInfosByTagLiveData()方法，监听同一个标签名下所以后台任务请求的运行结果，用法类似，这里不再阐述了

* 链式任务

> 知识WorkManager比较有特色的的地方

> 假设下面定义了先同步，在压缩，最后上传，这里可以通过链式任务实现，代码如下：

```kotlin
val sync = ...
val compress = ...
val upload = ...
WorkManager.getInstance(this).beginWith(sync).then(compress).then(upload).enqueue()
```





> Jetpack还有很多其他深入的内容，这部分可以去看guolin大神的博客或者关注他的公众号



> Jetpack博客完更



