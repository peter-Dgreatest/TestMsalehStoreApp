package com.example.msalehstoreapp.network

import com.example.msalehstoreapp.database.entities.BranchDB
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class BranchContainer(val output: List<BranchUser>)

@JsonClass(generateAdapter = true)
data class BranchUser(val branch_id :String,
                      val branch_manager : String,
                      val branch_manager_id : String,
                      val branch_name:String,
                      val supervisor_id:String){
}



/**
 * Convert Network results to database objects
 */
fun BranchContainer.asDomainModel(): List<BranchUser> {
    return output.map {
        BranchUser(
            branch_id =it.branch_id,
            branch_manager = it.branch_manager,
            branch_manager_id = it.branch_manager_id,
            branch_name = it.branch_name,
            supervisor_id = it.supervisor_id)
    }
}

fun BranchContainer.asDatabaseModel(): Array<BranchDB> {
    return output.map {
        BranchDB (
            branch_id = it.branch_id,
            branch_manager = it.branch_manager,
            branch_manager_id = it.branch_manager_id,
            branch_name = it.branch_name,
            supervisor_id = it.supervisor_id)
    }.toTypedArray()
}
