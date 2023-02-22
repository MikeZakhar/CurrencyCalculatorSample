package com.zakorchook.currencytest.data.model

import com.google.gson.annotations.SerializedName

data class ExchangeResponse(
    val rate: Float,
    @SerializedName("txt")
    val currencyName: String,
    @SerializedName("cc")
    val currencyCode: String
)
