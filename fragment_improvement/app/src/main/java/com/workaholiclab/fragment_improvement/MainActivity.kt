package com.workaholiclab.fragment_improvement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rad_dialog_type.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rad_dialog_alter->{
                    Toast.makeText(this,"alter",Toast.LENGTH_SHORT).show()
                }
                R.id.rad_dialog_time->{
                    Toast.makeText(this,"time",Toast.LENGTH_SHORT).show()
                }
                R.id.rad_dialog_date->{
                    Toast.makeText(this,"date",Toast.LENGTH_SHORT).show()
                }
                else->null
            }

        }
    }

    }
