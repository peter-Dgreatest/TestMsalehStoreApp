package com.example.msalehstoreapp.viewmodel.adminEnrollViewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.msalehstoreapp.database.dao.BranchDAO


class AdminEnroll1ViewModelFactory (
    private val dataSource: BranchDAO,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminEnroll1ViewModel::class.java)) {
            return AdminEnroll1ViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}