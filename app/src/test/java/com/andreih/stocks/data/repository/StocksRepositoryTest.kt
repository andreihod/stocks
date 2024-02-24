package com.andreih.stocks.data.repository

import com.andreih.stocks.commom.Result
import com.andreih.stocks.data.dao.StocksDao
import com.andreih.stocks.data.entity.StockEntity
import com.andreih.stocks.data.entity.StockQuoteEntity
import com.andreih.stocks.data.model.*
import com.andreih.stocks.network.YahooFinanceNetworkDataSource
import com.andreih.stocks.network.model.NetworkQuote
import com.andreih.stocks.network.model.NetworkStock
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StocksRepositoryTest {
    private lateinit var subjectSuccess: StocksRepository
    private lateinit var subjectError: StocksRepository
    private val stocksDao = object : StocksDao {
        override suspend fun listStocks(): List<StockEntity> = listOf()
        override suspend fun insertStock(stock: StockEntity) {}
        override suspend fun deleteStockBySymbol(stockSymbol: String) {}
        override suspend fun allQuotes(stockSymbols: List<String>) = listOf<StockQuoteEntity>()
        override suspend fun insertQuotes(quotes: List<StockQuoteEntity>) {}
    }

    companion object {
        private val networkStockList = listOf(
            NetworkStock(
                name = "ACME Inc.",
                longName = "ACME Inc.",
                symbol = "ACME",
                score = 1202.0,
                exchangeSymbol = "NYQ",
                exchangeName = "NYSE",
                sector = "Fun"
            )
        )

        private val expectedStockList = listOf(
            Stock(
                name = StockName("ACME Inc."),
                longName = StockLongName("ACME Inc."),
                symbol = StockSymbol("ACME"),
                exchangeSymbol = StockExchangeSymbol("NYQ"),
                exchangeName = StockExchangeName("NYSE"),
                sector = StockSectorName("Fun")
            )
        )

        private val networkStockQuotes = listOf(
            NetworkQuote(
                symbol = "AAPL",
                shortName = "Apple Inc.",
                longName = "Apple Inc.",
                currency = "USD",
                exchangeTimezoneName = "New_York/US",
                regularMarketChange = 1.2,
                regularMarketChangePercent = 0.023,
                regularMarketPrice = 102.20,
                regularMarketDayHigh = 103.1,
                regularMarketDayLow = 101.98,
                regularMarketVolume = 329292.02,
                regularMarketPreviousClose = 100.21,
                marketState = "OPEN"
            )
        )

        private val expectedStockQuotes = listOf(
            StockQuote(
                symbol = StockSymbol("AAPL"),
                stockName = StockName("Apple Inc."),
                stockLongName = StockLongName("Apple Inc."),
                currency = StockQuoteCurrency("USD"),
                timezone = StockQuoteTimezone("New_York/US"),
                marketChange = 1.2,
                marketChangePercent = 0.023,
                marketPrice = 102.20,
                marketDayHigh = 103.1,
                marketDayLow = 101.98,
                marketVolume = 329292.02,
                marketPreviousClose = 100.21,
                marketState = StockQuoteMarketState("OPEN")
            )
        )

        private val throwableError = Exception("Something happened")
    }


    @Before
    fun init() {
        subjectSuccess = StocksRepositoryImpl(
            object : YahooFinanceNetworkDataSource {
                override suspend fun search(query: String): List<NetworkStock> {
                    return networkStockList
                }

                override suspend fun quotes(symbols: List<StockSymbol>): List<NetworkQuote> {
                    return networkStockQuotes
                }
            }, stocksDao
        )

        subjectError = StocksRepositoryImpl(
            object : YahooFinanceNetworkDataSource {
                override suspend fun search(query: String): List<NetworkStock> {
                    throw throwableError
                }

                override suspend fun quotes(symbols: List<StockSymbol>): List<NetworkQuote> {
                    throw throwableError
                }
            }, stocksDao
        )
    }

    @Test
    fun search_flowsListOfResultsWithSuccessAccordingly() = runTest {
        val result = subjectSuccess.search("ACME").toList()

        assertEquals(
            listOf(
                Result.Loading,
                Result.Success(expectedStockList)
            ),
            result
        )
    }

    @Test
    fun search_flowsListOfResultsWithErrorAccordingly() = runTest {
        val result = subjectError.search("ACME").toList()

        assertEquals(
            listOf(
                Result.Loading,
                Result.Error(throwableError)
            ),
            result
        )
    }

    @Test
    fun quotes_flowsListOfResultsWithSuccessAccordingly() = runTest {
        val result = subjectSuccess.remoteQuotes(listOf(StockSymbol("AAPL"))).toList()

        assertEquals(
            listOf(
                Result.Loading,
                Result.Success(expectedStockQuotes)
            ),
            result
        )
    }

    @Test
    fun quotes_flowsListOfResultsWithErrorAccordingly() = runTest {
        val result = subjectError.remoteQuotes(listOf(StockSymbol("AAPL"))).toList()

        assertEquals(
            listOf(
                Result.Loading,
                Result.Error(throwableError)
            ),
            result
        )
    }
}