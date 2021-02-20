# 探索Fragment

> 2021.2.18
>
> Gary哥哥的哥哥

> 能够兼顾手机和平板的开发是我们尽可能做到的事情
>
> * Fragment可以让界面在平板上更好的展示

## Fragment是什么

* 嵌入在Activity中的UI片段
* 它能让程序更加合理充分的利用大屏幕空间
* 可以当成是一个mini activity



## Fragment的使用方式

> 首先我们要有一个平板模拟器，这里我选择Pixel C平板模拟器

### 简单用法

> 首先我们屏幕左右各一个Fragment

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#00ff00">
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="center_horizontal"
    android:textSize="24sp"
    android:text="This is the left fragment"
    android:id="@+id/textView1"/>
</LinearLayout>
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="center_horizontal"
    android:text="Button"
    android:id="@+id/button1"
    android:textAllCaps="false"/>
</LinearLayout>
```

**新建左右相关的类都继承Fragment**

> 注意：
>
> * 请使用AndroidX库的Fragment，内置版本的那个已经在android9.0的时候已经被抛弃了
> * 创建新项目的时候勾选 Use androidx.* artifiacts选项即可

> 我们左边为例来说，右边道理是一样的

```kotlin
class LeftFragment:Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.left_fragment,container,false)
    }
}
```

* 重写onCrateView方法，通过这个方法将刚才定义的left_fragment布局**动态加载进来**即可

在activity_main.xml中：

* **手动导入两个Fragment**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

   <fragment
       android:layout_width="0dp"
       android:layout_height="match_parent"
       android:name="com.workaholiclab.useoffragment.LeftFragment"
       android:layout_weight="1"
       android:id="@+id/leftFrag"/>

    <fragment
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:name="com.workaholiclab.useoffragment.RightFragment"
        android:layout_weight="1"
        android:id="@+id/rightFrag"/>

</LinearLayout>
```

> 注意：要有name属性说明是哪个class

``````xml
android:name="com.workaholiclab.useoffragment.LeftFragment"
``````

![img](https://img-blog.csdnimg.cn/20200405105039265.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1NvcGhvcmFl,size_16,color_FFFFFF,t_70)

### 动态添加Fragment

> 上面的方法是静态添加Fragment,其实实际开发中动态添加灵活性更高更好用

我们新写一个left的Fragment xml文件：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#00ff00">
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="center_horizontal"
    android:textSize="24sp"
    android:text="This is another left fragment"/>
</LinearLayout>
```

值得注意的是，这里没有id属性了

```kotlin
class AnotherLeftFragment:Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.another_left_fragment,container,false)
    }
}
```

在activity_main.xml文件当中：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <FrameLayout
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:id="@+id/leftLayout"/>

    <fragment
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:name="com.workaholiclab.useoffragment.RightFragment"
        android:layout_weight="1"
        android:id="@+id/rightFrag"/>

</LinearLayout>
```

```xml
    <FrameLayout
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:id="@+id/leftLayout"/>
```

> **==特别要注意id属性值==**

MainActivity中：

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button1.setOnClickListener {
            replaceFragment(AnotherLeftFragment())
        }
            replaceFragment(LeftFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager=supportFragmentManager
        val transaction=fragmentManager.beginTransaction()
        transaction.replace(R.id.leftLayout,fragment)
        transaction.commit()
    }
}
```



> **==核心代码：==**

```kotlin
private fun replaceFragment(fragment: Fragment) {
        val fragmentManager=supportFragmentManager
        val transaction=fragmentManager.beginTransaction()
        transaction.replace(R.id.leftLayout,fragment)
        transaction.commit()
    }
```

> 来到这里我们需要总结一下动态创建Fragment的流程了
>
> * 前期准备(写好xml文件)
>   * 写好布局文件
>   * 加到activity_main.xml里面就OK了

* 创建待定Fragment的实例（继承Fragment类）
* 获取FragmentManager，在Activity中可以直接调用getSupportFragmentManager方法获取
* 开启一个事物，通过beginTransaction开启
* 想容器内添加或替换Fragment，一般通过replace方法实现
  * 第一个参数:传入容器的id
  * 待定Fragment实例
* 提交事务，调用commit()方法即可



### 在Fragment中实现返回栈

> 按下Back键返回到上一个Fragment

非常简单，只需要加上一行代码即可：

```kotlin
        transaction.addToBackStack(null)
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button1.setOnClickListener {
            replaceFragment(AnotherLeftFragment())
        }
            replaceFragment(LeftFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager=supportFragmentManager
        val transaction=fragmentManager.beginTransaction()
        transaction.replace(R.id.leftLayout,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
```

```        transaction.addToBackStack()```方法可以接受一个用于描述返回栈状态

* 一般传入null即可

### Fragment和Activity之间的交互

> 实际上他们之间的联系确实没有那么紧密，他们都是在不同的类当中的



> 因此为了方便交互，FragmentManager提供了一个类似findViewById的方法
>
> 如下：

```kotlin
        val fragment=supportFragmentManager.findFragmentById(R.id.rightFrag)as RightFragment
```

或者：

> **但更加推荐下面的一种写法**

```kotlin
val fragment=rightFrag as RightFragment
```



> 在每个fragment当中都可以调用和当前Fragment相联系的Activity实例

```kotlin
if(activity!=null){
    val mainActivity=activity as MainActivity
}
```



**同样的，不同的Fragment也间是可以通信的！！！** **==我们使用Activity作为载体即可==**



## Fragment的生命周期

> 这里我就不再文字描述了
>
> 上图片：

![fragmentlife1](E:\kotlin-study\Studying-Kotlin\Fragment\fragmentlife1.jpg)

![fragmentlife2](E:\kotlin-study\Studying-Kotlin\Fragment\fragmentlife2.jpg)



同样Fragment也可以像前面的Activity一样，通过saveInstanceState保存数据

> 前面的例子如下：
>
> ## Activity被回收了怎么办
>
> > 如果A被回收掉了，从B返回A后，仍然可以显示A，但是不会知心onRestart()方法，==而是执行A的onCreate()的方法==，相当于A重新创建了一次
>
> **onSaveInstanceState()回调方法**，在回收之前被调用，对临时数据进行保存：
>
> ```kotlin
>   override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
>         super.onSaveInstanceState(outState, outPersistentState)
>         val tempData="Something you just typed"
>         outState.putString("data_key",tempData)
>     }
>     override fun onCreate(savedInstanceState: Bundle?) {
>         //一般这个Bundle都是空的，现在不是空了！！！
>         super.onCreate(savedInstanceState)
>         setContentView(R.layout.first_layout)
>         if (savedInstanceState!=null){
>             val tempData=savedInstanceState.getString("data_key")
>         }
>     }
> ```
>
> > 可以先将数据保存在Bundle对象中，再将这个对象存放到Intent中。来到目标中再将数据一一去出
> >
> > * 需要注意的是，在横竖屏转化的过程当中，会调用onCreate()的方法，但不推荐用上面的方法来解决，我们后面的章节会降到更好更加优雅的解决方法。







## 动态加载布局的技巧



### 使用限定符

> 在平板上，很多界面都是采取双页模式，像wechat的平板端一样，而手机的屏幕只有一个页面。
>
> 如何让程序对双页单页进行判断呢，这里就要采用限定符了

* 先只保留右侧的fragment

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
    
    <fragment
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:name="com.workaholiclab.useoffragment.RightFragment"
        android:id="@+id/rightFrag"/>

</LinearLayout>
```

* 接着在新建layout-large文件夹新建一个两个fragment的布局,同样创建同名的activity_main.xml

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:app="http://schemas.android.com/apk/res-auto"
      xmlns:tools="http://schemas.android.com/tools"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      tools:context=".MainActivity">
  
      <fragment
          android:layout_width="0dp"
          android:layout_weight="1"
          android:layout_height="match_parent"
          android:name="com.workaholiclab.useoffragment.LeftFragment"
          android:id="@+id/leftFrag"/>
      <fragment
          android:layout_width="0dp"
          android:layout_weight="3"
          android:layout_height="match_parent"
          android:name="com.workaholiclab.useoffragment.RightFragment"
          android:id="@+id/rightFrag"/>
  
  </LinearLayout>
  ```

  

> 这样子就大功告成了

下面看看Android的一些常用的限定符：

  ![](E:\kotlin-study\Studying-Kotlin\Fragment\限定符.jpg)



### 使用宽度最小限定符

> 上面的large到底指多大呢？
>
> 有时候我们希望更加灵活地为不同设备加载布局，不管他是不是被认定为large



**这时我们要是用最小宽度限定符**

* 允许我们对屏幕的宽度制定一个最小值，然后以这个最小值为灵界点加载布局

res目录下新建layout-sw600dp文件夹，同样创建activity_main.xml

> 意味着我们以600dp为一个最小宽度分界，**==大于==**这个值则会加载下面这个布局

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <fragment
        android:layout_width="0dp"
        android:layout_weight="1"
        android:layout_height="match_parent"
        android:name="com.workaholiclab.useoffragment.LeftFragment"
        android:id="@+id/leftFrag"/>
    <fragment
        android:layout_width="0dp"
        android:layout_weight="3"
        android:layout_height="match_parent"
        android:name="com.workaholiclab.useoffragment.RightFragment"
        android:id="@+id/rightFrag"/>

</LinearLayout>
```



## Fragment最佳实践

> 这里我们以编写一个简易版本的新闻应用为例

* 分隔开的细线是通过View来实现的

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent" android:layout_height="match_parent">
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:id="@+id/contentLayout"
    android:orientation="vertical"
    android:visibility="invisible"
    >
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:padding="10dp"
        android:textSize="20sp"
        android:id="@+id/newsTitle"
        />
    <View
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:background="#000"
        />

    <TextView
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:padding="15dp"
        android:textSize="18sp"
        android:id="@+id/newsContent"/>
</LinearLayout>
    <View
        android:layout_width="1dp"
        android:layout_height="match_parent"
        android:layout_alignParentLeft="true"
        android:background="#000"
</RelativeLayout>
```

> 我们设定成invisible表示单页模式下不点开来是不会显示出来的

```kotlin
class NewsContentActivity : AppCompatActivity() {
    companion object{
        fun actionStart(context: Context,title:String,content:String){
            val intent=Intent(context,NewsContentActivity::class.java).apply {
                putExtra("news_title",title)
                putExtra("news_content",content)
            }
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_content)
        val title=intent.getStringExtra("news_title")
        val content=intent.getStringExtra("news_content")
        if (title!=null&&content!=null)
        {
            val fragment=newsContentFrag as NewsContentFragment
            fragment.refresh(title,content)//刷新NewsContentFragment界面
        }
    }
}
```

```kotlin
class NewsContentFragment:Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inflater.inflate(R.layout.news_content_frag,container,false)
    }
    
    fun refresh(title:String,content:String)
    {
        contentLayout.visibility=View.VISIBLE
        //刷新内容
        newsContent.text=content
        newsTitle.text=title
    }
}
```

**如果想在单页模式下使用：**

* activity_news_content.xml（新建）

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">
<fragment
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:id="@+id/newsContentFrag"
    android:name="com.workaholiclab.bestparcticefragment.NewsContentFragment"/>
</LinearLayout>
```

这里我们充分发挥了代码的复用性，直接在布局当中引入NewsContentFragment



> 接下开我们还需要一个显示新闻列表的布局

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">
<androidx.recyclerview.widget.RecyclerView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:id="@+id/newsTitleRecyclerView"/>
</LinearLayout>
```

它的子布局：

```android:ellipsize="end"```用于设定当前文本内容超出控件宽度时候文本的缩略方式，这里指尾部缩略

```xml
<?xml version="1.0" encoding="utf-8"?>
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/newsTitle" android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:maxLines="1"
    android:ellipsize="end"
    android:textSize="18sp"
    android:paddingLeft="10dp"
    android:paddingRight="10dp"
    android:paddingBottom="10dp"
    android:paddingTop="10dp"
    />

```

新建Fragment:

```kotlin
class NewsTitleFragment:Fragment(){
    private var isTwopane=false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.news_title_frag,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isTwopane=activity?.findViewById<View>(R.id.newsContentLayout)!=null
    }
}
```

* 这里我们通过onActivityCreted方法在Activity中能否找到对饮id的View，来判断当前是单页还是双页

activity_main.xml：

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/newsTitleLayout"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >

<fragment
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:name="com.workaholiclab.bestparcticefragment.NewsTitleFragment"
    android:id="@+id/newsTitleFrag"/>

</FrameLayout>
```

同样新建一个sw600dp的activity_main.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="horizontal" android:layout_width="match_parent"
    android:layout_height="match_parent">
<fragment
    android:layout_width="0dp"
    android:layout_height="match_parent"
    android:layout_weight="1"
    android:name="com.workaholiclab.bestparcticefragment.NewsTitleFragment"
    android:id="@+id/newsTitleFrag"/>
    <FrameLayout
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_weight="3"
        android:id="@+id/newsContentLayout">
        <fragment
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:id="@+id/newsContentFrag"
            android:name="com.workaholiclab.bestparcticefragment.NewsContentFragment"/>
    </FrameLayout>
</LinearLayout>
```

> 注意：id是可以一样的

> 在往下做的过程中建议想清楚上面的步骤先

* 接下来我们就是做Adapter了

  > **==这理我们在NewsTitleFragment中新建一个内部内Adapter来适配==**

  ```kotlin
  class NewsTitleFragment:Fragment(){
      private var isTwopane=false
      override fun onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
      ): View? {
          return inflater.inflate(R.layout.news_title_frag,container,false)
      }
  
      override fun onActivityCreated(savedInstanceState: Bundle?) {
          super.onActivityCreated(savedInstanceState)
          isTwopane=activity?.findViewById<View>(R.id.newsContentLayout)!=null
      }
      
      inner class NewsAdapter(val newsList: List<News>):RecyclerView.Adapter<NewsAdapter.ViewHolder>(){
          inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
              val newsTitle:TextView=view.findViewById(R.id.newsTitle)
          }
  
          override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
              val view=LayoutInflater.from(parent.context).inflate(R.layout.news_item,parent,false)
              val holder=ViewHolder(view)
              holder.itemView.setOnClickListener{
                  val news=newsList[holder.adapterPosition]
                  if (isTwopane)
                  {
                      val fragment=newsContentFrag as NewsContentFragment
                      fragment.refresh(news.title,news.content)
                  }
                  else{
                      NewsContentActivity.actionStart(parent.context,news.title,news.content)
                  }
              }
              return holder
          }
  
          override fun getItemCount(): Int {
              return newsList.size
          }
  
          override fun onBindViewHolder(holder: ViewHolder, position: Int) {
              val news=newsList[position]
              holder.newsTitle.text=news.title
          }
      }
  }
  ```

  > 这里写成内部类的好处是可以直接访问NewsTitleFragment中的变量，如isTwoPane

* 收尾工作，像RecyclerView中填充数据

```kotlin
class NewsTitleFragment:Fragment(){
    private var isTwopane=false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.news_title_frag,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isTwopane=activity?.findViewById<View>(R.id.newsContentLayout)!=null
        val layoutManager=LinearLayoutManager(activity)
        newsTitleRecyclerView.layoutManager=layoutManager
        val adapter=NewsAdapter(getNews())
        newsTitleRecyclerView.adapter=adapter
    }

    private fun getNews(): List<News> {
        val newsList=ArrayList<News>()
        for(i in 1..50){
            val news=News("This is news title $i",getRandomLengthString("This is news content $i."))
            newsList.add(news)
        }
    }

    private fun getRandomLengthString(str: String): String {
        val n=(1..20).random()
        val builder=StringBuilder()
        repeat(n){
            builder.append(str)
        }
        return builder.toString()
    }

    inner class NewsAdapter(val newsList: List<News>):RecyclerView.Adapter<NewsAdapter.ViewHolder>(){
        inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
            val newsTitle:TextView=view.findViewById(R.id.newsTitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view=LayoutInflater.from(parent.context).inflate(R.layout.news_item,parent,false)
            val holder=ViewHolder(view)
            holder.itemView.setOnClickListener{
                val news=newsList[holder.adapterPosition]
                if (isTwopane)
                {
                    val fragment=newsContentFrag as NewsContentFragment
                    fragment.refresh(news.title,news.content)
                }
                else{
                    NewsContentActivity.actionStart(parent.context,news.title,news.content)
                }
            }
            return holder
        }

        override fun getItemCount(): Int {
            return newsList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val news=newsList[position]
            holder.newsTitle.text=news.title
        }
    }
}
```



> Fragment博客完工