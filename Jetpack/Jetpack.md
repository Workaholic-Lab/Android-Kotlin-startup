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
>         "${user.firstName} ${user.lastName}"
>     }
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
>         userId->
>         Repository.getUser(userId)
>     }
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
> * Dao。数据访问对象，通常会在这里对数据库的各项操作进行封装。实际开发过程中，逻辑层不需要和底层数据库大叫到了，直接和Dao层进行交互即可。
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

