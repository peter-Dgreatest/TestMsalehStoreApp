/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.msalehstoreapp.network

import com.example.msalehstoreapp.database.entities.LoginDB
import com.example.msalehstoreapp.domain.Users
import com.squareup.moshi.JsonClass

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 */

/**
 * VideoHolder holds a list of Videos.
 *
 * This is to parse first level of our network result which looks like
 *
 * {
 *   "user": []
 * }
 */
@JsonClass(generateAdapter = true)
    data class NetworkUserContainer(val output: List<NetworkUser>)

@JsonClass(generateAdapter = true)
    data class NetworkResponse(val output: String)

/**
 * Videos represent a devbyte that can be played.
 */
@JsonClass(generateAdapter = true)
data class NetworkUser(
    val id: Long,
    val username : String,
    val fxxname:String,
    val phxxone:String,
    val password: String,
    val user_type: String)

/**
 * Convert Network results to database objects
 */
fun NetworkUserContainer.asDomainModel(): List<Users> {
    return output.map {
        Users(
            userName = it.username,
            name = it.fxxname,
            phone = it.phxxone,
            passWord = it.password,
            id = it.id,
            userType = it.user_type)
    }
}

fun NetworkUserContainer.asDatabaseModel(): Array<LoginDB> {
    return output.map {
        LoginDB (
            userName = it.username,
            password = it.password,
            name = it.fxxname,
            phone = it.phxxone,
            id = it.id,
            loginType = it.user_type)
    }.toTypedArray()
}

fun NetworkResponse.asString(): String {
    return output
}
