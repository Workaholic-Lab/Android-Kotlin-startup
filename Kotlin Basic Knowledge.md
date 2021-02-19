# Kotlin Basic Knowledge

> 2021.2.19
>
> Gary哥哥的哥哥

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



# 延迟初始化

## 对变量延迟初始化

> 我们会发现在UI实践那部分的代码，对适配器的初始化有点特殊

```kotlin
package com.example.uibestpractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

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

我们把adapter设定为全局变量延迟初始化关键词: lateinit 

```private lateinit var adapter: MsgAdapter```

使用这个方法一定要保证你后面确实将此对象初始化！！！

* 两个小用法：

```kotlin
 adapter.notifyItemInserted(msgList.size - 1) // 当有新消息时，刷新RecyclerView中的显示
                    recyclerView.scrollToPosition(msgList.size - 1)  // 将 RecyclerView定位到最后一行
                    inputText.setText("") // 清空输入框中的内容
```

* 避免对同一个变量反复的初始化：

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
		...
        if (!::adapter.isInitialized) {
            adapter = MsgAdapter(msgList)
        }
        recyclerView.adapter = adapter
        send.setOnClickListener(this)
    }
```

```::adapter.isInitialized```表示==已经初始化==，这里对它取反即可



## 封闭类

> 我们利用封闭类优化代码

先来了解一下封闭类的具体作用

```kotlin
sealed class Result

class Success(val msg: String) : Result()

class Failure(val error: Exception) : Result()


fun getResultMsg(result: Result) = when (result) {
    is Success -> result.msg
    is Failure -> "Error is ${result.error.message}"
}
```



> sealed用interface来代替不好的地方：
>
> * ==要写else分支==
>
> * 如果我们增加一些分支条件的话就不太好处理了
>   * 我们多了一个Unknow而没有写上去的话，程序就会直接崩溃

封闭类是可继承的类，在后面加上一对括号

这时候 when语法不再需要else分支了，为什么不用写else了呢？

* **Kotlin编译器会自动检查封闭类有哪些子类，并且强制要求你将子类每一个所对应的条件都处理完**



## 优化代码

> 定义一个叫==MsgViewHolder==的封闭类

```kotlin
sealed class MsgViewHolder(view: View) : RecyclerView.ViewHolder(view)

class LeftViewHolder(view: View) : MsgViewHolder(view) {
    val leftMsg: TextView = view.findViewById(R.id.leftMsg)
}

class RightViewHolder(view: View) : MsgViewHolder(view) {
    val rightMsg: TextView = view.findViewById(R.id.rightMsg)
}
```





```kotlin
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



> 核心代码

```kotlin
 override fun onBindViewHolder(holder: MsgViewHolder, position: Int) {
        val msg = msgList[position]
        when (holder) {
            is LeftViewHolder -> holder.leftMsg.text = msg.content
            is RightViewHolder -> holder.rightMsg.text = msg.content
         }
    }
```



```kotlin
holder: MsgViewHolder
```

