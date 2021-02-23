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

