package com.example.msalehstoreapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.msalehstoreapp.database.entities.BranchDB


@Dao
interface BranchDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg user: BranchDB) : List<Long>



    @Query("SELECT * from branch")
    suspend fun getBranches() : Array<BranchDB>


    @Query("SELECT * from branch where branch_manager_id = :id")
    suspend fun getOneBranch(id : Long) : Array<BranchDB>

}