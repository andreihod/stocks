package com.andreih.stocks.network.retrofit

import com.andreih.stocks.BuildConfig
import com.andreih.stocks.network.MarketStackNetworkDataSource
import com.andreih.stocks.network.model.NetworkStock
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

private interface RetrofitMarketStackApi {
    @GET("tickers")
    suspend fun search(
        @Query("search") query: String,
        @Query("limit") limit: Int = 10
    ): NetworkSearchStockResponse
}

@Serializable
private data class NetworkSearchStockResponse(
    val data: List<NetworkStock>
)

@Singleton
class RetrofitMarketStackNetwork @Inject constructor(): MarketStackNetworkDataSource {

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    private val api = Retrofit.Builder()
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

                    println(url)

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
        .create(RetrofitMarketStackApi::class.java)

    override suspend fun search(query: String): List<NetworkStock> {
        return withContext(Dispatchers.IO) { api.search(query).data }
    }
}