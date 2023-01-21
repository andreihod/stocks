package com.andreih.stocks.network.retrofit

import com.andreih.stocks.network.MarketStackNetworkDataSource
import com.andreih.stocks.network.model.NetworkStock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

interface RetrofitMarketStack {
    @GET("tickers")
    suspend fun search(
        @Query("search") query: String,
        @Query("limit") limit: Int = 10
    ): NetworkSearchStockResponse
}

@Serializable
data class NetworkSearchStockResponse(
    val data: List<NetworkStock>
)

data class RetrofitMarketStackNetwork @Inject constructor(
    val api: RetrofitMarketStack
) : MarketStackNetworkDataSource {
    override suspend fun search(query: String): List<NetworkStock> {
        return withContext(Dispatchers.IO) { api.search(query).data }
    }
}