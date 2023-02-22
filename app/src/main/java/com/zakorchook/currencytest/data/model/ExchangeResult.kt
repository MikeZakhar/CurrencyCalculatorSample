package com.zakorchook.currencytest.data.model

data class ExchangeResult(
    val srcAmount: Float,
    val resultAmount: Float,
    val currencyCode: String,
    val currencyName: String
)
