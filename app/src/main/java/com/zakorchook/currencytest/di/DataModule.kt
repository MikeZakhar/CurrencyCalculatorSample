package com.zakorchook.currencytest.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.zakorchook.currencytest.Constants
import com.zakorchook.currencytest.data.db.AppDatabase
import com.zakorchook.currencytest.data.db.HistoryDao
import com.zakorchook.currencytest.data.network.RestApi
import com.zakorchook.currencytest.data.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideRestApi(): RestApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(OkHttpClient.Builder().addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            ).build())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
            .create(RestApi::class.java)
    }

    @Singleton
    @Provides
    fun provideHistoryDao(@ApplicationContext appContext: Context): HistoryDao {
        return Room.databaseBuilder(
            appContext, AppDatabase::class.java, "my-db"
        ).build().historyDao()
    }

    @Singleton
    @Provides
    fun provideRepository(restApi: RestApi, historyDao: HistoryDao): Repository {
        return Repository(restApi, historyDao)
    }
}