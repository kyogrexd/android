package com.example.lab9_3

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_calculate.setOnClickListener {
            when{
                ed_height.length() < 1 ->Toast.makeText(this,"請輸入身高",Toast.LENGTH_SHORT).show()
                ed_weight.length() < 1 ->Toast.makeText(this,"請輸入體重",Toast.LENGTH_SHORT).show()

                else -> MyTask(this).execute()
            }
        }
    }
    private class MyTask internal constructor(context: MainActivity) : AsyncTask<Void, Int, String>() {
        private val activityReference: WeakReference<MainActivity> = WeakReference(context)
        val activity = activityReference.get()

        override fun doInBackground(vararg params: Void?): String {
            var progress = 0

            while (progress <= 100){
                try {
                    Thread.sleep(50)
                    publishProgress(progress)
                    progress++
                }catch (e:InterruptedException){
                    e.printStackTrace()
                }
            }
            return "task finished"
        }
        override fun onPreExecute() {
            super.onPreExecute()
            if (activity != null) {
                activity.tv_weight.text = "標準體重\n無"
                activity.tv_bmi.text = "體脂肪\n無"

                activity.progressBar2.progress = 0
                activity.tv_progress.text = "0%"
                activity.ll_progress.visibility = View.VISIBLE
            }

        }
        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            values[0]?.let {
                if (activity != null) {
                    activity.progressBar2.progress = it
                    activity.tv_progress.text = "$it%"
                }

            }
        }

        override fun onPostExecute(result: String?) {
            if (activity != null) {
                activity.ll_progress.visibility = View.GONE
                val cal_height =  activity.ed_height.text.toString().toDouble()
                val cal_weight =  activity.ed_weight.text.toString().toDouble()

                val cal_standweight:Double
                val cal_bodyfat : Double

                if( activity.radioButton.isChecked){
                    cal_standweight = (cal_height - 80) * 0.7
                    cal_bodyfat = (cal_weight -0.88 * cal_standweight) / cal_weight * 100
                }else{
                    cal_standweight = (cal_height - 70) * 0.6
                    cal_bodyfat = (cal_weight -0.82 * cal_standweight) / cal_weight * 100
                }

                activity.tv_weight.text = "標準體重 \n${String.format("%.2f",cal_standweight)}"
                activity.tv_bmi.text = "體脂肪 \n${String.format("%.2f", cal_bodyfat)}"
            }
        }
    }
//    private fun runAsyncTask(){
//        object :AsyncTask<Void,Int,Boolean>(){
//            override fun onPreExecute() {
//                super.onPreExecute()
//                tv_weight.text = "標準體重\n無"
//                tv_bmi.text = "體脂肪\n無"
//
//                progressBar2.progress = 0
//                tv_progress.text = "0%"
//                ll_progress.visibility = View.VISIBLE
//            }
//
//            override fun doInBackground(vararg params: Void?): Boolean {
//                var progress = 0
//
//                while (progress <= 100){
//                    try {
//                        Thread.sleep(50)
//                        publishProgress(progress)
//                        progress++
//                    }catch (e:InterruptedException){
//                        e.printStackTrace()
//                    }
//                }
//                return true
//            }
//
//            override fun onProgressUpdate(vararg values: Int?) {
//                super.onProgressUpdate(*values)
//                values[0]?.let {
//                    progressBar2.progress = it
//                    tv_progress.text = "$it%"
//                }
//            }
//
//            override fun onPostExecute(result: Boolean?) {
//                ll_progress.visibility = View.GONE
//
//                val cal_height = ed_height.text.toString().toDouble()
//                val cal_weight = ed_weight.text.toString().toDouble()
//
//                val cal_standweight:Double
//                val cal_bodyfat : Double
//
//                if(radioButton.isChecked){
//                    cal_standweight = (cal_height - 80) * 0.7
//                    cal_bodyfat = (cal_weight -0.88 * cal_standweight) / cal_weight * 100
//                }else{
//                    cal_standweight = (cal_height - 70) * 0.6
//                    cal_bodyfat = (cal_weight -0.82 * cal_standweight) / cal_weight * 100
//                }
//
//                tv_weight.text = "標準體重 \n${String.format("%.2f",cal_standweight)}"
//                tv_bmi.text = "體脂肪 \n${String.format("%.2f", cal_bodyfat)}"
//            }
//        }
//}
}

