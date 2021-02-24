# 探索 ContentProvider

> 2021.2.23
>
> Gary哥哥的哥哥的哥哥

> **跨程序共享数据**
>
> 我们前面学到的持久化技术所保存的数据都只能在当前应用程序中访问
>
> 虽然SharedPreferences存储中提供了其他模式，但在早期的Android版本已经将其废弃，安全性也很差
>
> 下面我们推荐使用更加安全可靠的ContentProvider技术



## 简介

> 用于在不同的应用程序之间实现数据共享的功能，它提供一套完整的机制，同时确保访问数据的安全性
>
> 在正式学习ContentProvider前，我们需要先掌握另外一个非常重要的知识-----**Android的运行权限**

## 运行权限

> ==**这部分知识点文字比较多，详见书本p319页**==

![](E:\kotlin-study\Studying-Kotlin\ContentProvider\危险权限.jpg)





### 在运行时申请权限

> 为了简单起见，我们就使用CALL_PHONE这个权限来作为示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

   <Button
       android:layout_width="match_parent"
       android:layout_height="wrap_content"
       android:text="Make Call"
       android:id="@+id/makeCall"/>
</LinearLayout>
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        makeCall.setOnClickListener {
            try{
                val intent=Intent(Intent.ACTION_CALL)
                intent.data=Uri.parse("tel:10086")
                startActivity(intent)
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }
}
```

> 在注册文件中:

```XML
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.workaholiclab.runtimepermissiontest">
    <uses-permission android:name="android.permission.CALL_PHONE"/>

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

加了一句```<uses-permission android:name="android.permission.CALL_PHONE"/>```



* 这样子是无法成功地，由于权限被禁止所导致
* Android6.0开始，**系统在使用危险权限时必须进行运行时权限处理**

> 修改MainActivity的代码：

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        makeCall.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.CALL_PHONE),1)
            }else{
                call()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1->{
                if (grantResults.isNotEmpty()&&
                        grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    call()
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun call() {
        try {
            val intent=Intent(Intent.ACTION_CALL)
            intent.data=Uri.parse("tel:10086")
            startActivity(intent)
        }catch (e:IOException){
            e.printStackTrace()
        }
    }
}
```

* 上面这个代码看似有点复杂，让我们慢慢讲解

> 具体流程如下：

1. 判断用户是不是已经给过我们权限了，借助ContextCompat.checkSelfPermission()方法

```kotlin
ContextCompat.checkSelfPermission(this,android.Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED
```

* p1:Context
* p2：具体的权限名
* 这里与PackageManager.PERMISSION_GRANTED做对比，相等说明已经授权

2. 拨打电话的逻辑封装到call方法当中，**如果没有授权则需要调用ActivityCompat.requestPermissions()**方法来申请权限

``````
 ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.CALL_PHONE),1)
``````

requestPermissions: 

* p1:Activity实例
* p2：String数组，申请的权限名
* p3：唯一值即可，这里传入1

3. 权限申请对话窗口

   * 不管你按哪个选项，都会调用onRequestPermissionsResult方法
   * 授权的结果放在grantResults
   * 判断一下授权的结果即可：

   ```kotlin
   override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
           super.onRequestPermissionsResult(requestCode, permissions, grantResults)
           when(requestCode){
               1->{
                   if (grantResults.isNotEmpty()&&
                           grantResults[0]==PackageManager.PERMISSION_GRANTED){
                       call()
                   }else{
                       Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show()
                   }
               }
           }
       }
   ```

> 下面进入ContentProvider





## 访问其他程序中的数据

> ContentProvider的用法一般有两种
>
> * 使用现有的ContentProvider读取和操作相应程序中的数据
> * 创建自己的ContentProvider对其数据提供外部访问的接口，方便其他应用程序访问

### ContentProvider的基本用法

> 借助ContentResolver类，**Context中的getContentResolver()方法可以获取该类的实例！！！**
>
> * 其提供了一些列的对数据的增删改查操作，与前面SQLite的方法类似，只不过在参数上有稍微的一些区别

ContentResolver中的增删改查方法都是**不接收表名参数的**，而是**使用一个Uri参数代替**

URI主要由authority和path构成：

* authority是不同应用程序作区分的，为了避免冲突，会采用包名的方式进行命名 包名.provider
* path则是同一应用程序里面对不同的table做区分命名为/table1
* ∴结合起来就是ex: com.example.app.provider/table1

**但标准URI的格式如下：**

content://com.example.app.provider/table1

content://com.example.app.provider/table2



解析成URI对象的方法也很简单

```kotlin
val uri=Uri.parse("content://com.example.app.provider/table1")
```

> 对table1表中数据进行查询如下

```kotlin
val cursor=contentResolver.query(uri,projection,selection,selectionArgs,sortOrder)
```

查询完成后返回的依然是一个cursor对象，然后取出每一行中相应列的数据，如下：

```kotlin
while(cursor.moveToNext()){
    val column1= cursor.getString(cursor.getColumnIndex("column1"))
    val column2= cursor.getString(cursor.getColumnIndex("column2"))
}
cursor.close()
```

> **==其他CRUD操作具体见书本p328==**





### 实践：读取系统联系人

> 由于我们在模拟器中操作，我们需要在模拟器中手动加两个联系人先

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <ListView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/contactsView"/>

</LinearLayout>
```

> 为了让代码少一点，好看一点，我们使用ListView来做示范，当然RecyclerView也是完全可以的

> **==记得注册文件加上==**

```xml
<uses-permission android:name="android.permission.READ_CONTACTS"/>
```



```kotlin

class MainActivity : AppCompatActivity() {

    private val contactsList=ArrayList<String>()
    private lateinit var adapter:ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,contactsList)
        contactsView.adapter=adapter
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CONTACTS),1)
        }else{
            readContacts()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1->{
                if (grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    readContacts()
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun readContacts() {
        //查询联系人
        contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)?.apply {
            while (moveToNext()) {
                //获取联系人
                val displayName =
                    getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number =
                    getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactsList.add("$displayName\n$number")
            }
            adapter.notifyDataSetChanged()
            close()
        }
    }
}
```

> 这里我们没有用到Uri.prase()了，因为这个权限的用法本来就Android帮我们封装好了



## 创建自己的ContentProvider

> 继承ContentProvider的时候，需要重写6个抽象方法

```kotlin
class MyContentProvider: ContentProvider() {
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        TODO("Not yet implemented")
    }

    override fun onCreate(): Boolean {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }
}
```

> 这几个方法和之前SQLite的很类似，这里就不再展开来一一讲解了
>
> **==详见书本p333页==**
>
> * getType()根据传入的内容URI返回相应的MIME类型

很多方法里带有uri这个参数，正好这个参数是用来调用ContentResolver的CRUD操作的。

现在们需要对传入的uri参数进行解析，从中分析出调用方期望访问的表和数据

content://com.example.app.provider/table1/1

> 除了上面说过的不加id的写法，后面加多一个id为第二种写法，id为1的数据



通配符分别匹配两种格式的URI

* *表示匹配任意长度的任意字符
* #表示匹配任意长度的数字

> example:

* 匹配任意表的内容

content://com.example.app.provider/*

* 匹配table1表中任意一行数据的内容URI

content://com.example.app.provider/table1/#



> **接着，我们借助UriMatcher这个类轻松实现匹配内容URI的功能**



* 有一个addURI（）方法,可以分别发authority，path和一个自定义代码传进去。这样当调用UriMatcher的match()方法是，就可以将一个Uri对象春如，返回值是某个能够匹配这个Uri对象所对应的自定义代码，利用这个代码，我们就可以判断出调用放期望访问的是哪张表中的数据，我们还是搞点示例代码更加清楚

```kotlin

class MyContentProvider: ContentProvider() {
    private val table1Dir=0
    private val table1Item=1
    private val table2Dir=2
    private val table2Item=3
    
    private val uriMatcher=UriMatcher(UriMatcher.NO_MATCH)
    
    init {
        uriMatcher.addURI("com.workaholiclab.app.provider","table1",table1Dir)
        uriMatcher.addURI("com.workaholiclab.app.provider","table1/#",table1Item)
        uriMatcher.addURI("com.workaholiclab.app.provider","table2",table2Dir)
        uriMatcher.addURI("com.workaholiclab.app.provider","table2/#",table2Item)
    }
    
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        when(uriMatcher.match(uri)){
            table1Dir->{
                //查询table1表的所有数据
            }
            table1Item->{
                //查询table1表中的单条数据
            }
        table2Dir->{
                //查询table2表的所有数据
            }
            table2Item->{
                //查询table2表中的单条数据
            }
        }
    }

    override fun onCreate(): Boolean {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }
}
```

**getType()方法：**

用于获取Uri对象所对应的MINE类型。

> 一个内容URI的MIME字符串主要由三部分组成，Android对这三个部分做了如下格式规定：

* 必须以vnd开头
* 如果内容URI以路径结尾，则后接android.cursor.dir/;如果内容URI以id结尾，则后面接上android.cursor.item/。
* 最后接上 vnd.<authority>.<path>。

> example:

* 对于上面content://com.example.app.provider/table1 这个内容URI，它所对应的MIME类型就可以写成：

```vnd.android.cursor.dir/vnd.com.example.app.provider.table1```

* 对于content://com.example.app.provider/table1/1则可以写成

```vnd.android.cursor.item/vnd.com.example.app.provider.table1```

下面我们对getType()方法中的逻辑继续完善，代码如下所示：

```kotlin

class MyContentProvider: ContentProvider() {
    private val table1Dir=0
    private val table1Item=1
    private val table2Dir=2
    private val table2Item=3

    private val uriMatcher=UriMatcher(UriMatcher.NO_MATCH)

    init {
        uriMatcher.addURI("com.workaholiclab.app.provider","table1",table1Dir)
        uriMatcher.addURI("com.workaholiclab.app.provider","table1/#",table1Item)
        uriMatcher.addURI("com.workaholiclab.app.provider","table2",table2Dir)
        uriMatcher.addURI("com.workaholiclab.app.provider","table2/#",table2Item)
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        when(uriMatcher.match(uri)){
            table1Dir->{
                //查询table1表的所有数据
            }
            table1Item->{
                //查询table1表中的单条数据
            }
        table2Dir->{
                //查询table2表的所有数据
            }
            table2Item->{
                //查询table2表中的单条数据
            }
        }
    }

    override fun onCreate(): Boolean {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun getType(uri: Uri)=when(uriMatcher.match(uri)) {
        table1Dir->"vnd.android.cursor.dir/vnd.com.workaholic.app.provider.table1"
        table1Item->"vnd.android.cursor.item/vnd.com.workaholic.app.provider.table1"
        table2Dir->"vnd.android.cursor.dir/vnd.com.workaholic.app.provider.table1"
        table2Item->"vnd.android.cursor.item/vnd.com.workaholic.app.provider.table2"
        else -> null
    }
}
```

> 到这里就搞定了，任意一个应用程序都可以使用ContentResolver访问我们应用程序的数据
>
> * 为了保证隐私数据不要泄露出去，这里也不知不觉的解决了这个问题，因为我们不可能像UriMatcher中添加隐私数据的URI，所以这部分数据根本无法被外部程序访问，安全问题也就不存在了

> 下面来尝试实战一下吧

## 实战：实现跨程序数据共享

> 我们还是在上一章节中的DatabaseTest项目的基础上继续开发

> 在包名下New-> Other ->Content Provider

==**下面这个代码确实比较长，我们后续会慢慢讲解的！！！**==

> **==若讲解得不够细致，也可以看书本p339==**

```kotlin
package com.workaholiclab.databasetest

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class DatabaseProvider : ContentProvider() {
    private val bookDir=0
    private val bookItem=1
    private val categoryDir=2
    private val categoryItem=3
    private val authority="com.workaholiclab.databasetest.provider"
    private var dbHelper:MyDatabaseHelper?=null

    private val uriMatcher by lazy {
        val matcher=UriMatcher(UriMatcher.NO_MATCH)
        matcher.addURI(authority,"book",bookDir)
        matcher.addURI(authority,"book/#",bookItem)
        matcher.addURI(authority,"category",categoryDir)
        matcher.addURI(authority,"category/#",categoryItem)
        matcher
    }


    //创建
    override fun onCreate()=context?.let {
        dbHelper=MyDatabaseHelper(it,"BookStore.db",2)
        true
    }?:false

    //查询数据
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    )=dbHelper?.let {
        val db = it.readableDatabase
        val cursor=when(uriMatcher.match(uri)){
            bookDir->db.query("Book",projection,selection,selectionArgs,null,null,sortOrder)
            bookItem->{
                val bookId=uri.pathSegments[1]
                db.query("Book",projection,"id = ?", arrayOf(bookId),null, null,sortOrder)
            }
            categoryDir->db.query("Category",projection,selection,selectionArgs,null,null,sortOrder)
            categoryItem->{
                val categoryId=uri.pathSegments[1]
                db.query("Category",projection,"id = ?", arrayOf(categoryId),null,null,sortOrder)
            }
            else->null
        }
        cursor
    }

    //插入（添加）数据
    override fun insert(uri: Uri, values: ContentValues?)=dbHelper?.let {
        val db=it.writableDatabase
        val uriReturn=when(uriMatcher.match(uri)){
            bookDir,bookItem->{
                val newBookId=db.insert("Book",null,values)
                Uri.parse("content://$authority/book/$newBookId")
            }
            categoryDir,categoryItem->{
                val newCategoryId=db.insert("Category",null,values)
                Uri.parse("content://$authority/category/$newCategoryId")
            }
            else->null
        }
        uriReturn
    }


    //更新
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    )=dbHelper?.let {
        val db = it.writableDatabase
        val updatedRows=when(uriMatcher.match(uri)){
            bookDir->db.update("Book",values,selection,selectionArgs)
            bookItem->{
                val bookId=uri.pathSegments[1]
                db.update("Book",values,"id = ?", arrayOf(bookId))
            }
            categoryDir->db.update("Category",values,selection,selectionArgs)
            categoryItem->{
                val categoryId=uri.pathSegments[1]
                db.update("Category",values,"id = ?", arrayOf(categoryId))
            }
            else ->null
        }
        updatedRows
    }?:0


    //删除数据
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?)=dbHelper?.let { 
        val db=it.writableDatabase
        val deletedRows=when(uriMatcher.match(uri)){
            bookDir->db.delete("Book",selection,selectionArgs)
            bookItem->{
                val bookId=uri.pathSegments[1]
                db.delete("Book","id = ?", arrayOf(bookId))
            }
            categoryDir->db.delete("Category",selection,selectionArgs)
            categoryItem->{
                val categoryId=uri.pathSegments[1]
                db.delete("Category","id = ?", arrayOf(categoryId))
            }
            else -> 0
        }
        deletedRows
    }?:0
    
    
    override fun getType(uri: Uri)=when(uriMatcher.match(uri)){
        bookDir->"vnd.android.cursor.dir/vnd.com.workaholiclab.datebasetest.provider.book"
        bookItem->"vnd.android.cursor.item/vnd.com.workaholiclab.datebasetest.provider.book"
        categoryDir->"vnd.android.cursor.dir/vnd.com.workaholiclab.datebasetest.provider.category"
        categoryItem->"vnd.android.cursor.item/vnd.com.workaholiclab.datebasetest.provider.category"
        else->null
    }

}

```

