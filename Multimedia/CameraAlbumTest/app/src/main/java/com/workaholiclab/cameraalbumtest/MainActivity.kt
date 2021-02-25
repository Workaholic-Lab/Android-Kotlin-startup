package com.workaholiclab.cameraalbumtest

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    val takePhoto = 1
    lateinit var imageUri :Uri
    lateinit var outputImage: File

    val fromAlbum = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        takePhotoBtn.setOnClickListener {
            //创建File对象，用于存储拍照后的图片

            outputImage = File(externalCacheDir,"output_image.jpg")//采用应用管理目录来缓存（避免读写SD卡的危险权限）
            if(outputImage.exists()){
                outputImage.delete()
            }
            outputImage.createNewFile()
            imageUri = if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
                //将File对象转化成一个封装过的Uri对象（FileProvider对数据进行了保护）
                FileProvider.getUriForFile(this,"com.workaholiclab.cameraalbumtest.fileprovider",outputImage)
            }else{
                Uri.fromFile(outputImage) //该设备低于android7就调用Uri的fromFile方法将File转话为Uri对象
                //这个Uri对象包含了这张图片的真实存在的路径
            }
            //启动相机程序
            val intent =Intent("android.media.action.IMAGE_CAPTURE")
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)//指定图片的输入地址，这里为刚刚的Uri对象
            startActivityForResult(intent,takePhoto)
        }

        //选取相册
        fromAlbumBtn.setOnClickListener {
            //打开文件选择器
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            //指定只显示图片
            intent.type ="image/*"
            startActivityForResult(intent,fromAlbum)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            takePhoto->{
                if (resultCode ==Activity.RESULT_OK){
                    //将拍摄的照片显示出来
                    val bitmap= BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))//将这张图片解析称为Bitmap对象
                    imageView.setImageBitmap(rotateIfRequired(bitmap))//变成ImageView，需要注意一些手机上拍照转化会发生一些旋转，需要处理一下
                }
            }

            fromAlbum->{
                if(resultCode == Activity.RESULT_OK && data != null){
                    data.data?.let {
                        uri ->
                        //将选择的图片显示
                        val bitmap=getBitmapFromUri(uri)
                        imageView.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver
        .openFileDescriptor(uri,"r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }

    //照片旋转处理
    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)
        return when(orientation){
            ExifInterface.ORIENTATION_ROTATE_90->rotateBitmap(bitmap,90)
            ExifInterface.ORIENTATION_ROTATE_180->rotateBitmap(bitmap,180)
            ExifInterface.ORIENTATION_ROTATE_270->rotateBitmap(bitmap,270)
            else-> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix =Matrix()
        matrix.postRotate(degree.toFloat())
        val rotateBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true)
        bitmap.recycle()//将不再需要的Bitmap对象回收
        return rotateBitmap
    }
}