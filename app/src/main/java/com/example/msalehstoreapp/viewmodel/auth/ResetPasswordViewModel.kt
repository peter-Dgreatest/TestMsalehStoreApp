package com.example.msalehstoreapp.viewmodel.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.msalehstoreapp.database.dao.LoginDatabaseDao
import com.example.msalehstoreapp.repository.UserRepository
import kotlinx.coroutines.*
import com.example.msalehstoreapp.network.helpers.Resource

class ResetPasswordViewModel(
    val database: LoginDatabaseDao,
    application: Application,
    val emailString: String
) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    private val _loginResponse: MutableLiveData<Resource<String>> = MutableLiveData()
    val loginResponse: LiveData<Resource<String>>
        get() = _loginResponse

    // private val database = AppDatabase.getInstance(application)
    private val loginRepository = UserRepository(database = database, application)

    private val _success = MutableLiveData<Boolean>()

    val success: LiveData<Boolean>
        get() = _success


    private val _loginsuccess = MutableLiveData<Boolean>()

    init {
        _success.value = false
        _loginsuccess.value = false
    }

    fun resetPass(
        unmae: String, resetCode: String,
        pass1: String, pass2: String
    ) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _loginResponse.value = loginRepository.resetPass(
            unmae, resetCode,
            pass1
        )
    }

    override fun onCleared() {
        super.onCleared()
        _loginResponse.value=null
        viewModelJob.cancel()
    }


    fun closeViewHolder(){
        this.onCleared()
    }

}