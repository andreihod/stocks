package com.andreih.stocks.network

import com.andreih.stocks.data.model.StockSymbol
import com.andreih.stocks.network.model.NetworkQuote
import com.andreih.stocks.network.model.NetworkStock

interface MarketStackNetworkDataSource {
    suspend fun search(query: String): List<NetworkStock>
}