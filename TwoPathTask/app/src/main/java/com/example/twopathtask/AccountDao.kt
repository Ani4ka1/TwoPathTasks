package com.example.twopathtask

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AccountDao {
    @Insert
    suspend fun insert(account: Account)

    @Update
    suspend fun update(account: Account)

    @Query("SELECT * FROM accounts WHERE username = :username AND password = :password")
    suspend fun getAccount(username: String, password: String): Account?
}
