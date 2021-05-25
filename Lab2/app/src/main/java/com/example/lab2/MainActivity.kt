package com.example.lab2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start.setOnClickListener{
            if(gamer.length()<1){
                status.text = "請輸入玩家姓名"
            }
            else{
                name.text = "名字\n${gamer.text}"
                mystatus.text = "我方出拳\n${if(scissors.isChecked) "剪刀" else if(Rock.isChecked) "石頭" else "布"}"
                var computer = (Math.random()*3).toInt()
                pcstatus.text = "電腦出拳\n${if(computer == 0) "剪刀" else if(computer == 1)"石頭" else "布"}"

                when{
                    scissors.isChecked && computer ==2 || Rock.isChecked && computer == 0 || Paper.isChecked && computer == 1 ->{
                        winner.text = "勝利者\n${gamer.text}"
                        status.text = "恭喜你獲勝了!"
                    }
                    scissors.isChecked && computer ==1 || Rock.isChecked && computer == 2 || Paper.isChecked && computer == 0 ->{
                        winner.text = "勝利者\n電腦"
                        status.text = "電腦獲勝了!"
                    }
                    else -> {
                        winner.text = "勝利者\n平手"
                        status.text = "平局，在試一次"
                    }
                }
            }
        }

    }
}