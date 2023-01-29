package com.andreih.stocks.data.repository

import com.andreih.stocks.commom.asResult
import com.andreih.stocks.commom.Result
import com.andreih.stocks.data.dao.StocksDao
import com.andreih.stocks.data.entity.StockEntity
import com.andreih.stocks.data.model.Stock
import com.andreih.stocks.data.model.StockQuote
import com.andreih.stocks.data.model.StockSymbol
import com.andreih.stocks.network.MarketStackNetworkDataSource
import com.andreih.stocks.network.model.NetworkStock
import com.andreih.stocks.network.model.intoStock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface StocksRepository {
    suspend fun fetchQuote(symbol: StockSymbol): Flow<Result<StockQuote>>

    suspend fun search(query: String): Flow<Result<List<Stock>>>

    suspend fun addSymbol(symbol: StockSymbol)

    suspend fun removeSymbol(symbol: StockSymbol)

    suspend fun streamSymbols(): Flow<Result<List<StockSymbol>>>
}

class StocksRepositoryImpl @Inject constructor(
    private val marketStackNetwork: MarketStackNetworkDataSource,
    private val stocksDao: StocksDao
) : StocksRepository {
    override suspend fun fetchQuote(symbol: StockSymbol): Flow<Result<StockQuote>> {
        TODO()
    }

    override suspend fun search(query: String): Flow<Result<List<Stock>>> =
        flow { emit(marketStackNetwork.search(query).map(NetworkStock::intoStock)) }.asResult()

    override suspend fun addSymbol(symbol: StockSymbol) {
        stocksDao.insert(StockEntity(0, symbol.value))
    }

    override suspend fun removeSymbol(symbol: StockSymbol) {
        stocksDao.deleteBySymbol(symbol.value)
    }

    override suspend fun streamSymbols(): Flow<Result<List<StockSymbol>>> =
        stocksDao
            .streamStocks()
            .map { it.map { stock -> StockSymbol(stock.symbol) } }
            .distinctUntilChanged()
            .asResult()
}