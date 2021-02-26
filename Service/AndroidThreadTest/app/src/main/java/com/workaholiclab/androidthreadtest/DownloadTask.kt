//package com.workaholiclab.androidthreadtest
//
//import android.os.AsyncTask
//import android.widget.Toast
//import java.io.IOException
//import java.lang.Exception
//
///**
// * @Description:
// * @author: Gary
// * @date: Created on 2021/02/26 9:51
// * @since: Kotlin 1.4
// * @modified by:
// */
//class DownloadTask : AsyncTask<Unit,Int,Boolean>() {
//
//    override fun onPreExecute() {
//        progressDialog.show()//显示进度对话框
//
//    }
//
//    override fun doInBackground(vararg params: Unit?)=try{
//        while (true){
//            val downloadPercent = doDownload()//这是一个虚构的方法
//            publishProgress(downloadPercent)
//            if (downloadPercent>=100){
//                break
//            }
//        }
//        true
//    }catch (e:Exception){
//        e.printStackTrace()
//        false
//    }
//
//    override fun onProgressUpdate(vararg values: Int?) {
//        //在这里更新下载进度
//        progressDialog.setMessage("Downloaded ${values[0]}%")
//    }
//
//    override fun onPostExecute(result: Boolean) {
//        progressDialog.dismiss()//关闭进度条
//        //这里提示下载结果
//        if (result){
//            Toast.makeText(context,"Succeeded",Toast.LENGTH_SHORT).show()
//        }else{
//            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
//        }
//    }
//}