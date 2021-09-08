package com.example.msalehstoreapp.network.networkcalls

import com.example.msalehstoreapp.network.BranchContainer
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface AdminCalls {
   // @FormUrlEncoded
    @GET("getbranch/")
    fun getBranches():
            Deferred<BranchContainer>



}