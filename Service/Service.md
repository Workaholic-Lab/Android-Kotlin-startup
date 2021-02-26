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
* 　**onProgressUpdate(Progress...)：**当后台任务中调用了 publishProgress(Progress...)方法后，这个方法就会很快被调用，方法中携带的参数就是在后台任务中传递过来的。**在这个方法中可以对 UI 进行操作，利用参数中的数值就可以对界面元素进行相应地更新。**
* 　**onPostExecute(Result)：**当后台任务执行完毕并通过 return 语句进行返回时，这个方法就很快会被调用。返**回的数据会作为参数传递到此方法中，可以利用返回的数据来进行一些 UI 操作，**比如提醒任务执行的结果，以及关闭掉进度条对话框等。

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