package com.andreih.stocks.module

import com.andreih.stocks.BuildConfig
import com.andreih.stocks.network.MarketStackNetworkDataSource
import com.andreih.stocks.network.retrofit.RetrofitMarketStack
import com.andreih.stocks.network.retrofit.RetrofitMarketStackNetwork
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
abstract class MarketStackNetworkDataSourceModule {
    @Singleton
    @Binds
    abstract fun bindMarketStackNetworkDataSource(
        marketStackNetworkDataSource: RetrofitMarketStackNetwork
    ): MarketStackNetworkDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object RetrofitMarketStackModule {

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideJson() = Json { ignoreUnknownKeys = true; explicitNulls = false }

    @Provides
    @Singleton
    fun provideRetrofitMarketStack(json: Json): RetrofitMarketStack {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.MARKETSTACK_URL)
            .addConverterFactory(
                @OptIn(ExperimentalSerializationApi::class)
                json.asConverterFactory("application/json".toMediaType())
            )
            .client(
                OkHttpClient.Builder()
                    .addNetworkInterceptor {
                        val original = it.request()
                        val url = original
                            .url
                            .newBuilder()
                            .addQueryParameter("access_key", BuildConfig.MARKETSTACK_API_KEY)
                            .build()
                        it.proceed(original.newBuilder().url(url).build())
                    }
                    .addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BASIC
                        }
                    )
                    .build()
            )
            .build()
            .create(RetrofitMarketStack::class.java)
    }
}