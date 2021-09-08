package com.example.msalehstoreapp.ui.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.msalehstoreapp.R
import com.example.msalehstoreapp.repository.PrefRepository
import kotlinx.android.synthetic.main.activity_admin_enroll.*

class AdminEnrollActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_enroll)


        val pref = PrefRepository(application.applicationContext)

        tv_userName.setText(pref.getName())
        tv_loginType.setText(pref.getAcountType())

    }
}