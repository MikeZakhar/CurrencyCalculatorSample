package com.zakorchook.currencytest.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryEntity(
    val date: Long,
    val srcAmount: Float,
    val dstAmount: Float,
    val currencyName: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}