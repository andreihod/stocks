package com.andreih.stocks.network

import com.andreih.stocks.network.model.NetworkQuote
import com.andreih.stocks.network.model.NetworkStock

interface AlphaVantageNetworkDataSource {
    suspend fun searchStock(symbol: String): List<NetworkStock>

    suspend fun fetchQuote(symbol: String): NetworkQuote
}