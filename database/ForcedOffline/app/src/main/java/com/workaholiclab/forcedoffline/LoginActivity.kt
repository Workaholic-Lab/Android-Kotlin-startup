package com.workaholiclab.forcedoffline

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlin.math.log

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val prefs=getPreferences(Context.MODE_PRIVATE)
        val isRemember=prefs.getBoolean("remember_password",false)
        if(isRemember){
            //账号密码都放到文本框上
            val account=prefs.getString("account","")
            val password=prefs.getString("password","")
            accountEdit.setText(account)
            passwordEdit.setText(password)
            rememberPass.isChecked=true
        }
        login.setOnClickListener {
            val account=accountEdit.text.toString()
            val password=passwordEdit.text.toString()
            if(account=="admin"&&password=="123456")
            {
                val editor=prefs.edit()
                if(rememberPass.isChecked){
                    editor.putBoolean("remember_password",true)
                    editor.putString("account",account)
                    editor.putString("password",password)
                }else{
                    editor.clear()
                }
                editor.apply()
                val intent= Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this,"account or password is invalid",Toast.LENGTH_SHORT).show()
            }
        }
    }
}