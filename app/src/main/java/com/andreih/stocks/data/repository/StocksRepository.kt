package com.andreih.stocks.data.repository

import com.andreih.stocks.commom.asResult
import com.andreih.stocks.commom.Result
import com.andreih.stocks.data.dao.StocksDao
import com.andreih.stocks.data.entity.StockEntity
import com.andreih.stocks.data.entity.StockQuoteEntity
import com.andreih.stocks.data.entity.intoStockQuote
import com.andreih.stocks.data.model.Stock
import com.andreih.stocks.data.model.StockQuote
import com.andreih.stocks.data.model.StockSymbol
import com.andreih.stocks.data.model.intoStockQuoteEntity
import com.andreih.stocks.network.YahooFinanceNetworkDataSource
import com.andreih.stocks.network.model.NetworkQuote
import com.andreih.stocks.network.model.NetworkStock
import com.andreih.stocks.network.model.intoStock
import com.andreih.stocks.network.model.intoStockQuote
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface StocksRepository {
    suspend fun localQuotes(symbols: List<StockSymbol>): List<StockQuote>

    suspend fun remoteQuotes(symbols: List<StockSymbol>): Flow<Result<List<StockQuote>>>

    suspend fun saveRemoteQuotes(quotes: List<StockQuote>)

    suspend fun search(query: String): Flow<Result<List<Stock>>>

    suspend fun watchSymbol(symbol: StockSymbol)

    suspend fun unwatchSymbol(symbol: StockSymbol)

    fun flowWatchedSymbols(): Flow<List<StockSymbol>>
}

class StocksRepositoryImpl @Inject constructor(
    private val yahooFinanceNetwork: YahooFinanceNetworkDataSource,
    private val stocksDao: StocksDao
) : StocksRepository {
    private val stocksFlow = MutableStateFlow<List<StockEntity>?>(null)

    override suspend fun localQuotes(symbols: List<StockSymbol>): List<StockQuote> =
        stocksDao.allQuotes(symbols.map(StockSymbol::value)).map(StockQuoteEntity::intoStockQuote)

    override suspend fun remoteQuotes(symbols: List<StockSymbol>): Flow<Result<List<StockQuote>>> =
        flow { emit(yahooFinanceNetwork.quotes(symbols).map(NetworkQuote::intoStockQuote)) }.asResult()

    override suspend fun saveRemoteQuotes(quotes: List<StockQuote>) {
        stocksDao.insertQuotes(quotes.map(StockQuote::intoStockQuoteEntity))
    }

    override suspend fun search(query: String): Flow<Result<List<Stock>>> =
        flow { emit(yahooFinanceNetwork.search(query).map(NetworkStock::intoStock)) }.asResult()

    override suspend fun watchSymbol(symbol: StockSymbol) {
        stocksDao.insertStock(StockEntity(0, symbol.value))
        stocksFlow.emit(stocksDao.listStocks())
    }

    override suspend fun unwatchSymbol(symbol: StockSymbol) {
        stocksDao.deleteStockBySymbol(symbol.value)
        stocksFlow.emit(stocksDao.listStocks())
    }

    override fun flowWatchedSymbols(): Flow<List<StockSymbol>> =
        stocksFlow
            .onStart { emit(stocksDao.listStocks()) }
            .filterNotNull()
            .map { stock -> stock.map { StockSymbol(it.symbol) } }
}