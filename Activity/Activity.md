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

