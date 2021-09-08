package com.example.msalehstoreapp.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.msalehstoreapp.R
import com.example.msalehstoreapp.repository.PrefRepository
import com.example.msalehstoreapp.ui.admin.AdminDasboardActivity
import net.simplifiedcoding.ui.startNewActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)


        val prefRepository = PrefRepository(this)

        if(prefRepository.getLogin()){
            val activity =
                if (prefRepository.getAcountType() == "chiefadmin") AdminDasboardActivity::class.java
                else null// HomeActivity::class.java
            startNewActivity(activity!!)
        //    startActivity(Intent(this,activity))
        }
//        prefRepository.getLogin().asLiveData().observe(this, Observer {
//            val activity = if (it == null) AuthActivity::class.java else HomeActivity::class.java
//            startNewActivity(activity)
//        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
//        this.applicationContext.getColorStateList()
    }
}