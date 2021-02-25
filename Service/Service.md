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
> 对于这种情况Android提供了一套异步消息的处理机制，完美的解决在子线程中进行UI操作的问题，我们在下一小节去分析

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







#### Handler







#### MessageQueue





#### Looper



