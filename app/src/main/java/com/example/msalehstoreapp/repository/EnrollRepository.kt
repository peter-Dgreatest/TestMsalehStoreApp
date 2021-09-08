package com.example.msalehstoreapp.repository

import android.app.Application
import com.example.msalehstoreapp.database.dao.BranchDAO
import com.example.msalehstoreapp.database.entities.BranchDB
import com.example.msalehstoreapp.network.Network
import com.example.msalehstoreapp.network.asDatabaseModel
import com.example.msalehstoreapp.network.asDomainModel
import com.example.msalehstoreapp.network.helpers.Resource
import com.example.msalehstoreapp.network.helpers.SafeApiCall


class EnrollRepository(private val database: BranchDAO,
                       val application: Application
) : SafeApiCall {

    suspend fun getBranches() : Array<BranchDB> {
        var branch =
            database.getBranches()
        if(branch.isEmpty()) {

            val respose = getOnlineBranchInfo()
            when(respose){
                is Resource.Success->{
                    branch= respose.value
                    return branch
                }
            }
        }
        return branch
    }

    suspend fun getOnlineBranchInfo() = safeApiCall {
        val branchOn = Network(application.applicationContext).mnetworksadmin.getBranches().await()
        if (branchOn.asDomainModel().isNotEmpty()) {
            database.insert(*branchOn.asDatabaseModel())
        }
        branchOn.asDatabaseModel()
    }

}
