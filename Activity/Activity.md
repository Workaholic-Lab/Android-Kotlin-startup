# Activity 的生命周期

> 2021.1.14

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

## standard

> Android的==默认==启动模式