package com.andreih.stocks.data.repository

import com.andreih.stocks.commom.asResult
import com.andreih.stocks.commom.Result
import com.andreih.stocks.data.model.Stock
import com.andreih.stocks.data.model.StockQuote
import com.andreih.stocks.data.model.StockSymbol
import com.andreih.stocks.network.MarketStackNetworkDataSource
import com.andreih.stocks.network.model.NetworkStock
import com.andreih.stocks.network.model.intoStock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface StocksRepository {
    suspend fun fetchQuote(symbol: StockSymbol): Flow<Result<StockQuote>>

    suspend fun search(query: String): Flow<Result<List<Stock>>>
}

class StocksRepositoryImpl @Inject constructor(
    private val marketStackNetwork: MarketStackNetworkDataSource
) : StocksRepository {
    override suspend fun fetchQuote(symbol: StockSymbol): Flow<Result<StockQuote>> {
        TODO()
    }

    override suspend fun search(query: String): Flow<Result<List<Stock>>> =
        flow { emit(marketStackNetwork.search(query).map(NetworkStock::intoStock)) }.asResult()
}