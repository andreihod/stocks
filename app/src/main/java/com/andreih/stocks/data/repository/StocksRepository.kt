package com.andreih.stocks.data.repository

import com.andreih.stocks.commom.asResult
import com.andreih.stocks.commom.Result
import com.andreih.stocks.data.dao.StocksDao
import com.andreih.stocks.data.entity.StockEntity
import com.andreih.stocks.data.model.Stock
import com.andreih.stocks.data.model.StockQuote
import com.andreih.stocks.data.model.StockSymbol
import com.andreih.stocks.network.YahooFinanceNetworkDataSource
import com.andreih.stocks.network.model.NetworkQuote
import com.andreih.stocks.network.model.NetworkStock
import com.andreih.stocks.network.model.intoStock
import com.andreih.stocks.network.model.intoStockQuote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface StocksRepository {
    suspend fun quotes(symbols: List<StockSymbol>): Flow<Result<List<StockQuote>>>

    suspend fun search(query: String): Flow<Result<List<Stock>>>

    suspend fun watchSymbol(symbol: StockSymbol)

    suspend fun unwatchSymbol(symbol: StockSymbol)

    fun flowWatchedSymbols(): Flow<List<StockSymbol>>
}

class StocksRepositoryImpl @Inject constructor(
    private val yahooFinanceNetwork: YahooFinanceNetworkDataSource,
    private val stocksDao: StocksDao
) : StocksRepository {
    override suspend fun quotes(symbols: List<StockSymbol>): Flow<Result<List<StockQuote>>> =
        flow { emit(yahooFinanceNetwork.quotes(symbols).map(NetworkQuote::intoStockQuote)) }.asResult()


    override suspend fun search(query: String): Flow<Result<List<Stock>>> =
        flow { emit(yahooFinanceNetwork.search(query).map(NetworkStock::intoStock)) }.asResult()

    override suspend fun watchSymbol(symbol: StockSymbol) {
        stocksDao.insert(StockEntity(0, symbol.value))
    }

    override suspend fun unwatchSymbol(symbol: StockSymbol) {
        stocksDao.deleteBySymbol(symbol.value)
    }

    override fun flowWatchedSymbols(): Flow<List<StockSymbol>> =
        stocksDao
            .flowAllSymbols()
            .map { it.map { stock -> StockSymbol(stock.symbol) } }
            .distinctUntilChanged()
}