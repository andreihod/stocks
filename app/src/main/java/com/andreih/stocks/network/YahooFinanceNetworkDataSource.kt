package com.andreih.stocks.network

import com.andreih.stocks.network.model.NetworkStock

interface YahooFinanceNetworkDataSource {
    suspend fun search(query: String): List<NetworkStock>
}