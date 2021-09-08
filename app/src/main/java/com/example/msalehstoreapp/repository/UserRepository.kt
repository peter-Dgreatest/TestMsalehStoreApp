package com.example.msalehstoreapp.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.msalehstoreapp.database.dao.LoginDatabaseDao
import com.example.msalehstoreapp.database.entities.asDomainModel
import com.example.msalehstoreapp.domain.Users
import com.example.msalehstoreapp.network.Network
import com.example.msalehstoreapp.network.NetworkUserContainer
import com.example.msalehstoreapp.network.asDatabaseModel
import com.example.msalehstoreapp.network.asDomainModel
import com.example.msalehstoreapp.network.helpers.SafeApiCall

class UserRepository(private val database: LoginDatabaseDao,
                     val application: Application) : SafeApiCall {
    val user : LiveData<List<Users>> = Transformations.map(database.getLoginInfo()){
        it.asDomainModel()
    }

    private val prefRepository : PrefRepository by lazy {
        PrefRepository(application.applicationContext) }


    suspend fun getUserWithCred(uname:String,pass : String)  = safeApiCall {
        val userInfo =
            Network(application.applicationContext).mnetworks.getUsers(uname, pass).await()
        if (userInfo.asDomainModel().isNotEmpty()) {
            val prefRepository = PrefRepository(application.applicationContext)
            prefRepository.setLoggedIn(true)
            database.insert(*userInfo.asDatabaseModel())

        }
        userInfo
    }

    suspend fun getvcode(uname:String)=safeApiCall {
        val response =  Network(application.applicationContext).mnetworks2.getvcode(uname).await()
        response
    }



    fun putPrefs(suc: NetworkUserContainer) {
        prefRepository.setLoggedIn(true)
        prefRepository.setName(suc.asDomainModel().get(0).name)
        prefRepository.setAcountType(suc.asDomainModel().get(0).userType)
    }

    suspend fun resetPass(unmae: String, resetCode: String, pass1: String) =safeApiCall {
        val response =  Network(application.applicationContext).mnetworks2.resetpass(unmae,pass1,resetCode).await()
        if(response.contains("success")){
            database.clear()
        }
        response
    }
}