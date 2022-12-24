package com.andreih.stocks.network.retrofit

import com.andreih.stocks.BuildConfig
import com.andreih.stocks.network.AlphaVantageNetworkDataSource
import com.andreih.stocks.network.model.NetworkQuote
import com.andreih.stocks.network.model.NetworkStock
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

private interface RetrofitAlphaVantageApi {
    @GET("/")
    suspend fun fetchQuote(
        @Query("symbol") symbol: String,
        @Query("function") function: String = "GLOBAL_QUOTE"
    ): NetworkSearchQuote

    @GET("/")
    suspend fun searchStock(
        @Query("symbol") symbol: String,
        @Query("function") function: String = "SYMBOL_SEARCH"
    ): NetworkSearchStock
}

@Serializable
private data class NetworkSearchQuote(
    @SerialName("Global Quote")
    val quote: NetworkQuote,
)

@Serializable
private data class NetworkSearchStock(
    @SerialName("bestMatches")
    val matches: List<NetworkStock>
)

@Singleton
class RetrofitAlphaVantageNetwork @Inject constructor(): AlphaVantageNetworkDataSource {

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    private val api = Retrofit.Builder()
        .baseUrl(BuildConfig.ALPHA_VANTAGE_URL)
        .client(
            OkHttpClient.Builder()
                .addNetworkInterceptor {
                    val original = it.request()
                    val originalHttpUrl = original.url

                    val url = originalHttpUrl
                        .newBuilder()
                        .addQueryParameter("apikey", BuildConfig.ALPHA_VANTAGE_API_KEY)
                        .build()

                    it.proceed(original.newBuilder().url(url).build())
                }
                .build()
        )
        .addConverterFactory(
            @OptIn(ExperimentalSerializationApi::class)
            json.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(RetrofitAlphaVantageApi::class.java)

    override suspend fun searchStock(symbol: String): List<NetworkStock> {
        return withContext(Dispatchers.IO) {
            api.searchStock(symbol).matches
        }
    }

    override suspend fun fetchQuote(symbol: String): NetworkQuote {
        return withContext(Dispatchers.IO) {
            api.fetchQuote(symbol).quote
        }
    }
}