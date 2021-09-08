package com.example.msalehstoreapp.viewmodel.auth

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.msalehstoreapp.database.dao.LoginDatabaseDao

class ResetPasswordViewModelFactory(
    private val dataSource: LoginDatabaseDao,
    private val application: Application,
    val emailString: String
) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ResetPasswordViewModel::class.java)) {
                return ResetPasswordViewModel(dataSource, application,emailString) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
}