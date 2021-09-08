package com.example.msalehstoreapp.viewmodel.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.msalehstoreapp.database.dao.LoginDatabaseDao
import com.example.msalehstoreapp.repository.UserRepository
import kotlinx.coroutines.*
import com.example.msalehstoreapp.network.helpers.Resource

class ForgotPasswordViewModel(
    val database: LoginDatabaseDao,
    application: Application
) : AndroidViewModel(application) {


    private val viewModelJob = SupervisorJob()



    private val viewModelScope = CoroutineScope(viewModelJob+ Dispatchers.Main)


    private val _forgotPasswordResponse: MutableLiveData<Resource<String>> = MutableLiveData()
    val forgotPasswordResponse: LiveData<Resource<String>>
        get() = _forgotPasswordResponse

   // private val database = AppDatabase.getInstance(application)
    private val loginRepository = UserRepository(database = database,application)

    private val _success = MutableLiveData<Boolean>()

    val success: LiveData<Boolean>
        get() = _success



    init {
        _success.value=false
    }

    fun getvcode(uname:String){
        viewModelScope.launch {
            _forgotPasswordResponse.value = Resource.Loading
            _forgotPasswordResponse.value = loginRepository.getvcode(uname)
        }
    }

    fun doneNavigating() {
        _success.value=false
        _forgotPasswordResponse.value= Resource.Off
    }

    fun navigate(){
        _success.value=true
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}