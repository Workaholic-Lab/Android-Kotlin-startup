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

