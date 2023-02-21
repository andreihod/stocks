package com.andreih.stocks.network

import com.andreih.stocks.data.model.StockSymbol
import com.andreih.stocks.network.model.NetworkQuote
import com.andreih.stocks.network.model.NetworkStock

interface YahooFinanceNetworkDataSource {
    suspend fun search(query: String): List<NetworkStock>

    suspend fun quotes(symbols: List<StockSymbol>): List<NetworkQuote>
}