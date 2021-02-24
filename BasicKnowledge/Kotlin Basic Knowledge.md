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



# 扩展函数

> 不少高级语言都有扩展函数的功能，但Java并没有
>
> * 令人兴奋的是，Kotlin对扩展函数则有着很好的支持

> Example:统计一个字符串当中字母的数量

我们会自然地写出：

```kotlin
object StringUtil{
    fun lettersCount(str:String):Int{
        var count=0
        for(char in str){
            if(char.isLetter())
                count++
        }
        return count
    }
}
```



**上面这种写法固然是正确的，但有了扩展函数，我们可以用面向对象的思维来实现这个功能**

* 首先来看一下扩展函数的定义先

```kotlin
fun ClassName.methodName(p1:Int,p2:Int):Int{
    return 0
}
```

```kotlin
fun String.lettersCount():Int{
    var count=0
    for(char in this){
        if(char.isLetter())
            count++
    }
    return count
}
```

**这里我们把lettersCount函数定义为String类的扩展函数，那么函数就拥有了String实例的上下文**

* 这个this就代表了字符串本身

* 调用

```kotlin
val count="ABDJ1234989!@)))".lettersCount()
```

# 运算符重载

> 这里的定义和C++中的很类似，就不再过多阐述



> 我这里就简单的实现几个小实例

```kotlin
class Obj {
    operator fun plus(obj:Obj):Obj{
        //处理相加的逻辑代码
    }
}


val obj1=Obj()
val obj2=Obj()
val obj3= obj1+ obj2
```

**这里plus就代表了+，对应-就是minus**



==**这里调用函数的对照表如下：**==

| 表达式 | 对应的函数     | 说明   |
| ------ | -------------- | ------ |
| +a     | a.unaryPlus()  |        |
| -a     | a.unaryMinus() | 取反   |
| !a     | a.not()        | 非运算 |

| a++  | a.inc() | 自增 |
| ---- | ------- | ---- |
| a–   | a.dec() | 自减 |

| a + b | a.plus(b)                 | 加           |
| ----- | ------------------------- | ------------ |
| a - b | a.minus(b)                | 减           |
| a * b | a.times(b)                | 乘           |
| a / b | a.div(b)                  | 除           |
| a % b | a.rem(b), a.mod(b) (弃用) | 取模         |
| a..b  | a.rangeTo(b)              | 从a到b的区间 |

| a in b  | b.contains(a)  | b包含a   |
| ------- | -------------- | -------- |
| a !in b | !b.contains(a) | b不包含a |

| a[i]               | a.get(i)              | 获取位置i的值              |
| ------------------ | --------------------- | -------------------------- |
| a[i, j]            | a.get(i, j)           | 获取位置 i 和 j 的值       |
| a[i_1, …, i_n]     | a.get(i_1, …, i_n)    | 获取 i_1到i_n的值          |
| a[i] = b           | a.set(i, b)           | 将位置 i 的值设置为 b      |
| a[i, j] = b        | a.set(i, j, b)        | 将位置 i 和 j 的值设置为 b |
| a[i_1, …, i_n] = b | a.set(i_1, …, i_n, b) | 将 i_1到i_n 的值设为b      |

| a()            | a.invoke()            | 无参调用         |
| -------------- | --------------------- | ---------------- |
| a(i)           | a.invoke(i)           | 带一个参数的调用 |
| a(i, j)        | a.invoke(i, j)        | 带两个参数的调用 |
| a(i_1, …, i_n) | a.invoke(i_1, …, i_n) | 带n个参数的调用  |

| a += b | a.plusAssign(b)                       |      |
| ------ | ------------------------------------- | ---- |
| a -= b | a.minusAssign(b)                      |      |
| a *= b | a.timesAssign(b)                      |      |
| a /= b | a.divAssign(b)                        |      |
| a %= b | a.remAssign(b), a.modAssign(b) (弃用) |      |

| a == b | a?.equals(b) ?: (b === null)    |      |
| ------ | ------------------------------- | ---- |
| a != b | !(a?.equals(b) ?: (b === null)) |      |

| a > b  | a.compareTo(b) > 0  | a大于b     |
| ------ | ------------------- | ---------- |
| a < b  | a.compareTo(b) < 0  | a 小于b    |
| a >= b | a.compareTo(b) >= 0 | a大于等于b |
| a <= b | a.compareTo(b) <= 0 | a小于等于b |

> 示例：

```kotlin
operator fun String.times(n:Int)=repeat(n)
//实现上面的重载后即可
fun getRandomLengthString(str:String)=str*(1..20).random()
```



# 高阶函数

## 定义高阶函数

> 高阶函数和Lambda表达式是密不可分的

**定义：如果一个函数接收另一个函数作为参数，或者返回值的类型是另一个函数，那么该函数就是高阶函数**



* 基本语法：

  ```kotlin
  (String,Int)->Unit
  ```

  > * ->左边是声明该函数接收什么参数的
  >   * 如果不接收任何参数，写一对空括号就好了
  > * ->的右边用于返回类型，Unit就相当于Java中的void

  ```kotlin
  fun num1AndNum2(num1:Int,num2:Int,operation:(Int,Int)->Int):Int{
      return operation(num1,num2)
  }
  
  
  fun plus(num1:Int,num2:Int):Int{
      return num1+num2
  }
  
  fun minus(num1:Int,num2: Int):Int{
      return num1-num2
  }
  
  fun main(){
      val num1=80
      val num2=50
      val r1= num1AndNum2(num1,num2,::plus)
      val r2= num1AndNum2(num1,num2,::minus)
      println("r1 is $r1")
      println("r2 is $r2")
  
  }
  ```

  > 下面用Lambda表达式改进一下：

  ```kotlin
  fun main(){
      val num1=80
      val num2=50
      val r1= num1AndNum2(num1,num2){n1,n2->n1+n2}
      val r2= num1AndNum2(num1,num2){n1,n2->n1-n2}
      println("r1 is $r1")
      println("r2 is $r2")
  }
  ```

  

> 下面我们用我们自己创建的build函数代替之前的apply

```kotlin
fun StringBuilder.build(block:StringBuilder.()->Unit):StringBuilder{
    block()
    return this
}
```

你会看到这里加了个StringBuilder.是什么鬼，其实这才是完整的语法格式，加上ClassName表示这个·函数类型定义在哪个类中



```kotlin
fun main(){
    val list= listOf("Apple","Banana","Orange","Pear","Grape")
    val result=StringBuilder().build {
        append("Start eating fruits")
        for(fruit in list)
        {
            append(fruit).append("\n")
        }
        append("Ate all fruits")
    }
    println(result.toString())
}
```



## 内联函数

> 内联函数的机制可以将使用Lambda表达式带来的运行是开销完全解除

**加上fun前面inline关键字即可**

```kotlin
inline fun num1AndNum2(num1:Int,num2:Int,operation:(Int,Int)->Int):Int{
    return operation(num1,num2)
}
```



> 下面考虑一下特殊的情况：

![1](E:\kotlin-study\Studying-Kotlin\Basic Knowledge\1.jpg)







![2](E:\kotlin-study\Studying-Kotlin\Basic Knowledge\2.jpg)







![3](E:\kotlin-study\Studying-Kotlin\Basic Knowledge\3.jpg)



![4](E:\kotlin-study\Studying-Kotlin\Basic Knowledge\4.jpg)



# 高阶函数的应用

> 为了进行举例说明我们在本节中使用高阶函数简化SharedPrefernces和ContentValues两种API的用法

## 简化SharedPreference

> 原来：

```kotlin
val editor=getSharedPreferences("data", Context.MODE_PRIVATE).edit()
editor.putString("name","Wendy")
editor.putInt("age",20)
editor.putBoolean("married",false)
editor.apply()
```

> 简化：

```kotlin
private fun SharedPreferences.open(block:SharedPreferences.Editor.()->Unit){
    val editor=edit()
    editor.block()
    editor.apply()
}
```

```kotlin
 getSharedPreferences("data",Context.MODE_PRIVATE).open {
                putString("name","Wendy")
                putInt("age",20)
                putBoolean("married",false)
            }
```



> 实际上，

```xml
implementation 'androidx.core:core-ktx:1.0.2'
```

中有一个edit函数就是上面open的用法：

```kotlin
getSharedPreferences("data",Context.MODE_PRIVATE).edit {
                putString("name","Wendy")
                putInt("age",20)
                putBoolean("married",false)
            }
```



## 简化 ContentValues

> 原来：

```kotlin
val values=ContentValues().apply {
                    put("name","Game of Thrones")
                    put("author","George Martin")
                    put("pages",720)
                    put("price",20.85)
                }
```

> 简化：
>
> * Pair对象
>   * 就是A to B
> * vararg对应就是Java的可变参数列表
>   * 允许传入0,1,2...n个Pair对象
>   * 这些对象都会赋值到vararg变量当中去，用for-in来遍历即可



```kotlin
fun cvOf(vararg pairs: Pair<String,Any?>):ContentValues{
    
}
```

```kotlin
\fun cvOf(vararg pairs: Pair<String,Any?>):ContentValues{
    val cv=ContentValues()
    for(pair in pairs){
        val key=pair.first
        val value=pair.second
        when(value){
            is Int->cv.put(key,value)
            is Long->cv.put(key,value)
            is Short->cv.put(key,value)
            is Float->cv.put(key,value)
            is Double->cv.put(key,value)
            is Boolean->cv.put(key,value)
            is String->cv.put(key,value)
            is Byte->cv.put(key,value)
            is ByteArray->cv.put(key,value)
            null->cv.putNull(key)
        }
    }
}
```

> 在改进：用 apply函数

```kotlin
fun cvOf(vararg pairs: Pair<String,Any?>)=ContentValues().apply{
    for(pair in pairs){
        val key=pair.first
        val value=pair.second
        when(value){
            is Int->put(key,value)
            is Long->put(key,value)
            is Short->put(key,value)
            is Float->put(key,value)
            is Double->put(key,value)
            is Boolean->put(key,value)
            is String->put(key,value)
            is Byte->put(key,value)
            is ByteArray->put(key,value)
            null->putNull(key)
        }
    }
}
```

> 使用：

```kotlin
val values= cvOf("name" to "Game of Thrones","author" to "George Martin","pages" to 720,"price" to 20.85)
db.insert("Book",null,values)
```

* 同样在KTX库中有一个contentValuesOf的函数，用法和上面一样

```kotlin
val values= contentValuesOf("name" to "Game of Thrones","author" to "George Martin","pages" to 720,"price" to 20.85)
db.insert("Book",null,values)
```



# 泛型

> Kotlin的泛型和Java确实有类似的地方，也存在不一样的地方，我们看看基本用法：

> 泛型主要由两种定义
>
> * 定义泛型类
> * 定义泛型方法

定义一个泛型类 和创建对象实例

```kotlin
class MyClass<T> {
    fun method(param:T):T{
        return param
    }
}
val myClass=MyClass<Int>()
val result= myClass.method(123)
```

定义泛型方法

```kotlin
class MyClass {
    fun<T> method(param:T):T{
        return param
    }
}

val myClass=MyClass()
val result= myClass.method<Int>(123)
```

> 除此之外，Kotlin还允许我们对翻新的类型进行限制，可以指定上界类型，ex:Number
>
> 默认上界类型为Any?

```kotlin
class MyClass {
    fun<T:Number> method(param:T):T{
        return param
    }
}

val myClass=MyClass()
val result= myClass.method<Int>(123)
```



**==其他的泛型例子详见书本p346页==**



> 可以看出来Kotlin的泛型和Java的很类似，没什么大的区别，很好理解。



# 类的委托和委托属性

> 委托是一种设计模式，它的理念是：操作对象自己不会去处理某段逻辑，而是会把工作委托给另外一个辅助对象去处理，像C#这种语言就对委托进行了原生的支持

# 实现自己的lazy函数

> 由于时间原因，**这两部分的内容详解还是==见课本p347页吧==**