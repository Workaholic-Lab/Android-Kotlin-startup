# 基础UI界面设计

> 2021.2.16
>
> Gary哥哥的哥哥的哥哥

## 如何编写程序界面

> 通过编写XML的方式来实现
>
> * **不过Google推出的ConstraintLayout不是非常适合通过编写XML的方式来开发界面，而是适合在可视化编辑器下拖放控件来实现界面设计**
>   * **虽然Google现在更加倾向于让大家使用ConstraintLayout来开发程序界面**
>   * 但作为初学者，我们先对ConstraintLayout进行非常详细的讲解先。

> 下面是我用constraintLayout来编写的一个小小Demo，**详见Basic_UI_Demo的项目**

![](E:\kotlin-study\Studying-Kotlin\UI_demo\constraint.png)

## 常用控件

### TextView

> ```kotlin
>  match_parent表示当前控件的大小和父布局的大小一样
>  wrap_content表示让当前控件的大小刚好能够包含住里面的全部内容
> ```

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
<!--    match_parent表示当前控件的大小和父布局的大小一样; wrap_content表示让当前控件的大小刚好能够包含住里面的全部内容-->

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</LinearLayout>
```

> ```xml
> android:gravity="center_vertical|center_horizontal"
> ```

|： 指定多个属性值

> ==距离用dp做单位，字体用sp做单位==

* 其他属性我就不一一介绍了，我们在实践时候自己去探索吧

## Button

```xml
 <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/button1"
        android:text="Button1"
        />
```

> 默认Button上的字母都是大写，可以通过属性修改
>
> * ```android:textAllCaps="false"```

> 下面通过kotlin来响应事件

```kotlin
package com.workaholiclab.control

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Lambda表达式实现
        button1.setOnClickListener {
            //逻辑代码
            Log.d("respond","button1 is clicked")
        }
    }
}
```



> 除了上面这种Lambda表达式来实现之外，还可以函数API的方式来注册监听器

```kotlin
package com.workaholiclab.control

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

//函数API来注册实现
class MainActivity : AppCompatActivity(),View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button2.setOnClickListener(this)
        
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.button2->{
                Log.d("respond2","button2 is clicked")
            }
        }
    }
}
```

* 上面这种写法我们让MainActivity实现了View.OnClickListener接口，并且重写了onClick方法，然后在调用button的setOnClickListener方法将Activity实例传递出去。

* 每当我们点击按钮的时候就会执行Onclick的方法

 

## EditText

```xml
    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/editText1"
        />
```

> hint属性可以提示信息

```xml
 <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/editText1"
        android:hint="Type something here"
        />
```

> 为了防止输入东西过多，导致EditText非常难看，我们用android:maxLines的属性
>
> * 下面代码表示EditText可以==显示==两行，以前的行不再显示，但还在的，往上滚了

```xml
<EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/editText1"
        android:hint="Type something here"
        android:maxLines="2"
        />
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Lambda表达式实现
        button1.setOnClickListener {
            //逻辑代码
            val input=editText1.text.toString()
            Toast.makeText(this,input,Toast.LENGTH_SHORT).show()
        }
    }
}
```



## ImageView

> src指定目录的图片

```xml
  <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:id="@+id/imageView1"
        android:src="@drawable/abc_vector_test"
        />
```

```kotlin
//手动更改
button2.setOnClickListener { 
            imageView1.setImageResource(R.drawable.ic_launcher_foreground)
        }
```



## ProgressBar

> ProgressBar是作用在界面上的一个进度条表示我们的程序正在加载一些数据

```xml
 <ProgressBar
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/progressBar"
        />
```

> 那么，我们当数据加载完之后如何让他消失掉呢
>
> * 我们利用控件的可视化来做

```kotlin
 button1.setOnClickListener {
            val input=editText1.text.toString()
            Toast.makeText(this,input,Toast.LENGTH_SHORT).show()
            if(progressBar.visibility==View.VISIBLE){
                progressBar.visibility=View.GONE
            }
            else{
                progressBar.visibility=View.VISIBLE
            }
        }
```



**我们还可以给ProgressBar指定样式**

> 默认为转圈圈

下面改成水平的：

```xml
  <ProgressBar
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/progressBar"
        style="@style/Widget.AppCompat.ProgressBar.Horizontal"
        android:max="100"
        />
```

```kotlin
//增加进度   
button1.setOnClickListener {
            val input=editText1.text.toString()
            Toast.makeText(this,input,Toast.LENGTH_SHORT).show()
            if(progressBar.visibility==View.VISIBLE){
                progressBar.visibility=View.GONE
            }
            else{
                progressBar.visibility=View.VISIBLE
                progressBar.progress=progressBar.progress+10
            }
        }
```



## AlertDialog

> 在当前用户见面弹出一个**对话窗**
>
> * 这个控件一般用于非常重要的内容或者是一些警告信息
>   * 下面通过kotlin代码来做一个Dialog
>   * 最后记得show出来

```kotlin
 button2.setOnClickListener {
            imageView1.setImageResource(R.drawable.ic_launcher_foreground)
            AlertDialog.Builder(this).apply {
                setTitle("This is a Dialog")
                setMessage("something Important")
                setCancelable(false)
                setPositiveButton("OK"){
                    dialog, which ->
                }
                setNegativeButton("Cancel"){
                    dialog, which ->
                }
                show()
            }
        }
```

![](E:\kotlin-study\Studying-Kotlin\UI_demo\dialog.png)

> 空间部分我们先讲解那么多，其他控件及其属性可以通过实践来自行摸索

