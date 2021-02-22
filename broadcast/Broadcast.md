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



**特别提醒，不要再onReceive方法中添加非常复杂的逻辑代码，或者耗时的操作，因为BroadcastReceiver是不允许开启现成的，长时间不结束onReceive就会程序崩溃**



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

> 我们同股哟AnotherBroadcastReceiver来演示一下
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



