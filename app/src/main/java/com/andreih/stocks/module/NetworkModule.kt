package com.andreih.stocks.module

import com.andreih.stocks.BuildConfig
import com.andreih.stocks.network.YahooFinanceNetworkDataSource
import com.andreih.stocks.network.retrofit.RetrofitYahooFinance
import com.andreih.stocks.network.retrofit.RetrofitYahooFinanceNetwork
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class YahooFinanceNetworkDataSourceModule {
    @Singleton
    @Binds
    abstract fun bindYahooFinanceNetworkDataSource(
        yahooFinanceNetworkDataSource: RetrofitYahooFinanceNetwork
    ): YahooFinanceNetworkDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object RetrofitYahooFinanceModule {

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideJson() = Json { ignoreUnknownKeys = true; explicitNulls = false }

    @Provides
    @Singleton
    fun provideRetrofitYahooFinance(json: Json): RetrofitYahooFinance {
        return Retrofit.Builder()
            .baseUrl("https://${BuildConfig.X_RAPIDAPI_HOST}")
            .addConverterFactory(
                @OptIn(ExperimentalSerializationApi::class)
                json.asConverterFactory("application/json".toMediaType())
            )
            .client(
                OkHttpClient.Builder()
                    .addNetworkInterceptor {
                        val newRequest = it.request().newBuilder()
                            .addHeader("X-RapidAPI-Key", BuildConfig.X_RAPIDAPI_KEY)
                            .addHeader("X-RapidAPI-Host", BuildConfig.X_RAPIDAPI_HOST)
                            .build()
                        it.proceed(newRequest)
                    }
                    .addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BASIC
                        }
                    )
                    .build()
            )
            .build()
            .create(RetrofitYahooFinance::class.java)
    }
}