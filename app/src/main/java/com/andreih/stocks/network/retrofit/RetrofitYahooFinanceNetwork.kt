package com.andreih.stocks.network.retrofit

import com.andreih.stocks.network.YahooFinanceNetworkDataSource
import com.andreih.stocks.network.model.NetworkStock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

interface RetrofitYahooFinance {
    @GET("auto-complete")
    suspend fun search(
        @Query("q") query: String
    ): NetworkSearchStockResponse
}

@Serializable
data class NetworkSearchStockResponse(
    val quotes: List<NetworkStock>
)

data class RetrofitYahooFinanceNetwork @Inject constructor(
    val api: RetrofitYahooFinance
) : YahooFinanceNetworkDataSource {
    override suspend fun search(query: String): List<NetworkStock> {
        return withContext(Dispatchers.IO) {
            api.search(query).quotes.sortedByDescending(
                NetworkStock::score
            )
        }
    }
}