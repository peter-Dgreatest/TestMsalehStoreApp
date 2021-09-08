package com.example.msalehstoreapp.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.msalehstoreapp.R
import com.example.msalehstoreapp.repository.PrefRepository
import kotlinx.android.synthetic.main.activity_admin_dasboard.*

class AdminDasboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dasboard)


        val pref = PrefRepository(application.applicationContext)

        tv_userName.setText(pref.getName())
        tv_loginType.setText(pref.getAcountType())


        layout_enroll.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,AdminEnrollActivity::class.java))
        })
    }
}