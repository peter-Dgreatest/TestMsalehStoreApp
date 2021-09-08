package com.example.msalehstoreapp.network

import android.content.Context
import com.example.msalehstoreapp.R
import com.example.msalehstoreapp.network.networkcalls.AdminCalls
import com.example.msalehstoreapp.network.networkcalls.AuthNetworkService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Main entry point for network access. Call like `Network.mnetworks.getPlaylist()`
 */
class Network(context: Context) {

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(context.getString(R.string.login))
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    private val retrofit2 = Retrofit.Builder()
        .baseUrl(context.getString(R.string.login))
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val mnetworks = retrofit.create(AuthNetworkService::class.java)
    val mnetworks2 = retrofit2.create(AuthNetworkService::class.java)

    val mnetworksadmin = retrofit.create(AdminCalls::class.java)
}
