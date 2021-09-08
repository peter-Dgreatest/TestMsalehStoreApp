package com.example.msalehstoreapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.msalehstoreapp.database.entities.LoginDB

@Dao
interface LoginDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg user: LoginDB) : List<Long>

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param loginDB new value to write
     */
    @Update
    suspend fun update(loginDB: LoginDB)

    /**
     * Selects and returns the row that matches the supplied start time, which is our key.
     *
     * @param key startTimeMilli to match
     */
    @Query("SELECT * from login WHERE id = :key")
    suspend fun get(key: Long): LoginDB?

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM login")
    suspend fun clear()

    /**
     * Selects and returns all rows in the table,
     *
     */
    @Query("SELECT * FROM login ORDER BY id DESC")
    fun getLoginInfo(): LiveData<List<LoginDB>>

    /**
     * Selects and returns the login with given id.
     */
    @Query("SELECT * from login WHERE id = :key")
    fun getLoginWithId(key: Long): LiveData<LoginDB>
}
