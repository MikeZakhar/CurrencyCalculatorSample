package com.zakorchook.currencytest.data.repository

import com.zakorchook.currencytest.Constants
import com.zakorchook.currencytest.data.db.HistoryDao
import com.zakorchook.currencytest.data.db.HistoryEntity
import com.zakorchook.currencytest.data.model.ExchangeResult
import com.zakorchook.currencytest.data.network.RestApi
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class Repository @Inject constructor(
    private val restApi: RestApi,
    private val historyDao: HistoryDao
) {

    private val currencyCodes = emptyList<String>().toMutableList()

    private val apiDateFormat = SimpleDateFormat(Constants.API_DATE_FORMAT, Locale.US)

    suspend fun requestAllCurrencyCodes(): List<String> {
        if (currencyCodes.isEmpty())
            currencyCodes.addAll(
                restApi.exchange(null, null)
                    .map { it.currencyCode }
                    .sorted()
            )
        return currencyCodes
    }

    suspend fun exchange(currencyCode: String, srcAmount: Float, date: Date): ExchangeResult {
        val responseItem = restApi.exchange(
            currencyCode,
            apiDateFormat.format(date)
        ).firstOrNull()
            ?: throw Exception("No results for this date and currency")
        val dstAmount = srcAmount / responseItem.rate
        addToHistory(HistoryEntity(date.time, srcAmount, dstAmount, responseItem.currencyName))
        return ExchangeResult(
            srcAmount,
            dstAmount,
            responseItem.currencyCode,
            responseItem.currencyName
        )
    }

    suspend fun getHistory(): List<HistoryEntity> {
        return historyDao.getAll().reversed()
    }

    private suspend fun addToHistory(historyEntity: HistoryEntity) {
        historyDao.insertItem(historyEntity)
        historyDao.checkAndRemoveRedundant()
    }
}