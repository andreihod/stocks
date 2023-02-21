package com.andreih.stocks.network.retrofit

import com.andreih.stocks.data.model.StockSymbol
import com.andreih.stocks.network.YahooFinanceNetworkDataSource
import com.andreih.stocks.network.model.NetworkQuote
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

    @GET("market/v2/get-quotes")
    suspend fun quotes(
        @Query("symbols") symbols: String,
        @Query("region") region: String = "US"
    ): NetworkQuotesResponse
}

@Serializable
data class NetworkSearchStockResponse(
    val quotes: List<NetworkStock>
)

@Serializable
data class NetworkQuotesResponse(
    val quoteResponse: NetworkQuotesResult
)

@Serializable
data class NetworkQuotesResult(
    val result: List<NetworkQuote>
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

    override suspend fun quotes(symbols: List<StockSymbol>): List<NetworkQuote> {
        return withContext(Dispatchers.IO) {
            api.quotes(
                symbols.joinToString(
                    separator = ",",
                    transform = StockSymbol::value
                )
            ).quoteResponse.result
        }
    }
}