package com.andreih.stocks.data.repository

import com.andreih.stocks.commom.suspendRunCatching
import com.andreih.stocks.data.model.Stock
import com.andreih.stocks.data.model.StockQuote
import com.andreih.stocks.data.model.StockSymbol
import com.andreih.stocks.network.MarketStackNetworkDataSource
import com.andreih.stocks.network.model.NetworkStock
import com.andreih.stocks.network.model.intoStock
import javax.inject.Inject

interface StocksRepository {
    suspend fun fetchQuote(symbol: StockSymbol): Result<StockQuote>

    suspend fun search(query: String): Result<List<Stock>>
}

class StocksRepositoryImpl @Inject constructor(
    private val marketStackNetworkDataSource: MarketStackNetworkDataSource
) : StocksRepository {
    override suspend fun fetchQuote(symbol: StockSymbol): Result<StockQuote> {
        TODO()
    }

    override suspend fun search(query: String): Result<List<Stock>> {
        return suspendRunCatching {
            marketStackNetworkDataSource.search(query).map(NetworkStock::intoStock)
        }
    }
}