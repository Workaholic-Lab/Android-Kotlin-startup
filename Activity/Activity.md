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