package com.capgemini.workmanagerdemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.work.*

class MainActivity : AppCompatActivity() {

    lateinit var tv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv = findViewById(R.id.tv)

        val wManager = WorkManager.getInstance(this)

       val builder = OneTimeWorkRequestBuilder<StringSplitter>()

        val input = Data.Builder()
            .putString("longString", "John:Merry:Robert:MArk")
            .build()

        builder.setInputData(input)
        // configure constraints
        val wConstraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        builder.setConstraints(wConstraints)
        val req = builder.build()

        wManager.enqueue(req)
        var status = ""

        wManager.getWorkInfoByIdLiveData(req.id).observe(this) {
            if( it != null){
                when(it.state){
                    WorkInfo.State.ENQUEUED -> {
                        status = "ENQUEUED"
                    }
                    WorkInfo.State.RUNNING -> {
                        status = "RUNNING"
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        status = "SUCCESS"
                       val result = it.outputData.getStringArray("splits") ?: arrayOf("")
                        tv.append("\n Splits: ${result.contentToString()}")
                    }
                    WorkInfo.State.FAILED -> {
                        status = "FAILED"
                    }
                    else -> {
                        status = "UNKNOWN"
                    }
                }

                tv.append("\n $status")

            }
        }

    }
}

//"a:B:c:"
class StringSplitter(context: Context, params: WorkerParameters) : Worker(context, params){

    override fun doWork(): Result {
        // get input data
        // perform task with input
        // return result of task
        Thread.sleep(2000)

        val inputS = inputData.getString("longString") ?: ""
        Log.d("StringSplitter", "Input : $inputS")

        val splits = inputS.split(":")

        val outputData = Data.Builder()
            .putStringArray("splits", splits.toTypedArray())
            .build()

        return Result.success(outputData)
    }

}

