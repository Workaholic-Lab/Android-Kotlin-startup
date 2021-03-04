package com.workaholiclab.viewmodeltest

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*


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
            viewModel.counter++
            refreshCounter()
        }
        refreshCounter()
        clearBtn.setOnClickListener {
            viewModel.counter=0
            refreshCounter()
        }
    }

    override fun onPause() {
        super.onPause()
        val edit = sp.edit()
        edit.putInt("count_reserved",viewModel.counter)
        edit.commit()
    }

    private fun refreshCounter() {
        infoText.text=viewModel.counter.toString()
    }
}