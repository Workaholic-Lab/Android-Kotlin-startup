package com.example.ui_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class MainActivity : AppCompatActivity() {

    var yuwen:String=""
    var shuxue:String=""
    var yingyu:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var display:TextView=findViewById(R.id.textView)
        var buttonLeft:Button=findViewById(R.id.button)
        var button3:Button=findViewById(R.id.button3)
        var buttonRight:Button=findViewById(R.id.button2)
        var aSwitch:Switch=findViewById(R.id.switch1)
        var progressBar:ProgressBar=findViewById(R.id.progressBar3)
        var editText:EditText=findViewById(R.id.editTextNumber)
        var radioGroup:RadioGroup=findViewById(R.id.radio_group)
        var imageView:ImageView=findViewById(R.id.imageView)
        var seekBar:SeekBar=findViewById(R.id.seekBar)
        var checkBoxYuWen:CheckBox=findViewById(R.id.checkBox)
        var checkBoxShuXue:CheckBox=findViewById(R.id.checkBox2)
        var checkBoxYingYu:CheckBox=findViewById(R.id.checkBox3)
        var ratingBar:RatingBar=findViewById(R.id.ratingBar)

        buttonLeft.setOnClickListener{
            display.setText(R.string.button1)
        }
        buttonRight.setOnClickListener { display.setText(R.string.button2) }

        aSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                display.setText("开")
            }
            else display.setText("关")
        }

        button3.setOnClickListener {
            var s:String=editText.text.toString()
            if(s=="") s="0"
            progressBar.progress = s.toInt()
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId==R.id.radioButton){
                imageView.setImageResource(R.drawable.android_logo)
            }
            else imageView.setImageResource(R.drawable.apple_logo)
        }

        seekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                display.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        checkBoxYuWen.setOnCheckedChangeListener { buttonView, isChecked ->
            yuwen = if(isChecked) "语文"
            else ""
            display.text = yuwen+shuxue+yingyu
        }
        checkBoxShuXue.setOnCheckedChangeListener { buttonView, isChecked ->
            shuxue = if(isChecked) "数学"
            else ""
            display.text = yuwen+shuxue+yingyu
        }
        checkBoxYingYu.setOnCheckedChangeListener { buttonView, isChecked ->
            yingyu= if(isChecked) "英语"
            else ""
            display.text = yuwen+shuxue+yingyu
        }

        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            Toast.makeText(applicationContext,rating.toString(),Toast.LENGTH_SHORT).show()
        }
    }
}

