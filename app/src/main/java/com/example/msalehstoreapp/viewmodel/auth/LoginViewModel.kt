package com.example.msalehstoreapp.viewmodel.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.msalehstoreapp.database.dao.LoginDatabaseDao
import com.example.msalehstoreapp.domain.LoginCred
import com.example.msalehstoreapp.network.NetworkUserContainer
import com.example.msalehstoreapp.repository.UserRepository
import kotlinx.coroutines.*
import com.example.msalehstoreapp.network.helpers.Resource

class LoginViewModel(
    val database: LoginDatabaseDao,
    application: Application
) : AndroidViewModel(application) {


    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob+ Dispatchers.Main)


    private val _loginResponse: MutableLiveData<Resource<NetworkUserContainer>> = MutableLiveData()
    val loginResponse: LiveData<Resource<NetworkUserContainer>>
    get() = _loginResponse

   // private val database = AppDatabase.getInstance(application)
    private val loginRepository = UserRepository(database = database,application)

    private val _success = MutableLiveData<Boolean>()

    val success: LiveData<Boolean>
        get() = _success


    private val _loginsuccess = MutableLiveData<Boolean>()

    val loginsuccess: LiveData<Boolean>
        get() = _loginsuccess

    init {
        _success.value = false
        _loginsuccess.value = false
    }

    fun navigateToReset() {
        _loginsuccess.value=true
    }

    fun doneNavigatingLogin() {
        _loginsuccess.value =false
    }

    fun loginUser(lg : LoginCred)  =
        viewModelScope.launch {
            _loginResponse.value = Resource.Loading
            _loginResponse.value = loginRepository.getUserWithCred(lg.userName, lg.pass)
            
    }

    fun saveAccessTokens(it: NetworkUserContainer) {
        loginRepository.putPrefs(it)
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}
