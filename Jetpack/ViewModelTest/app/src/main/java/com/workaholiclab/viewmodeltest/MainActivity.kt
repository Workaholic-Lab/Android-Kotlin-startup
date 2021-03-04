package com.workaholiclab.viewmodeltest

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    lateinit var sp:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp=getPreferences(Context.MODE_PRIVATE)
        val countReserved = sp.getInt("count_reserved",0)
        setContentView(R.layout.activity_main)
        viewModel=ViewModelProviders.of(this,MainViewModelFactory(countReserved)).get(MainViewModel::class.java)
        plusOneBtn.setOnClickListener {
            viewModel.plusOne()
        }
        refreshCounter()
        clearBtn.setOnClickListener {
            viewModel.clear()
        }
        viewModel.counter.observe(this, Observer { count->infoText.text=count.toString() })

        getUserBtn.setOnClickListener {
            val userId =(0..10000).random().toString()
            viewModel.getUser(userId)
        }
        viewModel.user.observe(this, Observer { user->infoText.text=user.firstName })

        //数据库CRUD
        val userDao =AppDatabase.getDatabase(this).userDao()
        val user1=User("Tom","James",40)
        val user2=User("Tom","Jimmy",63)
        addDataBtn.setOnClickListener {
            thread{
                user1.id=userDao.insertUser(user1)
                user2.id=userDao.insertUser(user2)
            }
        }
        updateDataBtn.setOnClickListener {
            thread {
                user1.age=42
                userDao.updateUser(user1)
            }
        }
        deleteDataBtn.setOnClickListener {
            thread {
                userDao.deleteUserByLastName("Hanks")
            }
        }
        queryDataBtn.setOnClickListener {
            thread {
                for(user in userDao.loadAllUsers()){
                    Log.d("MainActivity",user.toString())
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val edit = sp.edit()
        edit.putInt("count_reserved",viewModel.counter.value?:0)
        edit.commit()
    }

    private fun refreshCounter() {
        infoText.text=viewModel.counter.toString()
    }
}