package com.zakorchook.currencytest.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    @Query("SELECT * FROM historyEntity")
    suspend fun getAll(): List<HistoryEntity>

    @Insert
    suspend fun insertItem(historyEntity: HistoryEntity)

    @Query("DELETE FROM historyEntity where id NOT IN (SELECT id from historyEntity ORDER BY id DESC LIMIT 10)")
    suspend fun checkAndRemoveRedundant()
}