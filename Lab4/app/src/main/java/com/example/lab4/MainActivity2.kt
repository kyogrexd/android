package com.example.lab4

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import kotlinx.android.synthetic.main.activity_main2.*
import android.widget.Toast

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        btn_send.setOnClickListener {
            if(ed_drink.length()<1){
                Toast.makeText(this,"請輸入飲料名稱",Toast.LENGTH_SHORT).show()
            }
            else{
                var b = Bundle()
                b.putString("drink",ed_drink.text.toString())
                b.putString("sugar",radioGroup1.findViewById<RadioButton>(radioGroup1.checkedRadioButtonId).text.toString())
                b.putString("ice",radioGroup2.findViewById<RadioButton>(radioGroup2.checkedRadioButtonId).text.toString())

                setResult(Activity.RESULT_OK, Intent().putExtras(b))
                finish()

            }
        }
    }
}