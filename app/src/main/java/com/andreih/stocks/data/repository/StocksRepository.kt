package com.andreih.stocks.data.repository

import com.andreih.stocks.commom.suspendRunCatching
import com.andreih.stocks.data.model.Stock
import com.andreih.stocks.data.model.StockQuote
import com.andreih.stocks.network.AlphaVantageNetworkDataSource
import com.andreih.stocks.network.model.NetworkStock
import com.andreih.stocks.network.model.intoStock
import com.andreih.stocks.network.model.intoStockQuote
import javax.inject.Inject

interface StocksRepository {
    suspend fun fetchQuote(symbol: String): Result<StockQuote>

    suspend fun searchStock(symbol: String): Result<List<Stock>>
}

class StocksRepositoryImpl @Inject constructor(
    private val alphaVantageNetworkDataSource: AlphaVantageNetworkDataSource
) : StocksRepository {
    override suspend fun fetchQuote(symbol: String): Result<StockQuote> {
        return suspendRunCatching {
            alphaVantageNetworkDataSource.fetchQuote(symbol).intoStockQuote()
        }
    }

    override suspend fun searchStock(symbol: String): Result<List<Stock>> {
        return suspendRunCatching {
            alphaVantageNetworkDataSource.searchStock(symbol).map(NetworkStock::intoStock)
        }
    }
}