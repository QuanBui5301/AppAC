package com.example.appac

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class MainActivity : AppCompatActivity() {
    companion object {
        const val PERMISSIONS_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Thread {
                Thread.sleep(500)
                val intent = Intent(applicationContext, LoginAct::class.java)
                startActivity(intent)
                finish()
        }.start()
    }
}