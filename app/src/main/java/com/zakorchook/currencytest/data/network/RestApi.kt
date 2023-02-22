package com.zakorchook.currencytest.data.network

import com.zakorchook.currencytest.data.model.ExchangeResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface RestApi {

    @GET("/NBUStatService/v1/statdirectory/exchange?json")
    suspend fun exchange(
        @Query("valcode") currencyCode: String?,
        @Query("date") date: String?
    ): List<ExchangeResponse>

}