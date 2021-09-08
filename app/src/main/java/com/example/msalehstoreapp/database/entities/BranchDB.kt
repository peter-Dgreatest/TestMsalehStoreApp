package com.example.msalehstoreapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "branch")
data class BranchDB constructor(
    @PrimaryKey
    val branch_id :String,
    val branch_manager : String,
    val branch_manager_id : String,
    val branch_name:String,
    val supervisor_id:String){
}
