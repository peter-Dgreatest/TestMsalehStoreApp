package com.example.msalehstoreapp.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.example.msalehstoreapp.R
import com.example.msalehstoreapp.ui.auth.LoginActivity
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        imageView2.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}