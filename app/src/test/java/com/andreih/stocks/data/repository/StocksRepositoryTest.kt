package com.andreih.stocks.data.repository

import com.andreih.stocks.data.model.*
import com.andreih.stocks.commom.Result
import com.andreih.stocks.data.dao.StocksDao
import com.andreih.stocks.data.entity.StockEntity
import com.andreih.stocks.network.YahooFinanceNetworkDataSource
import com.andreih.stocks.network.model.NetworkStock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StocksRepositoryTest {
    private lateinit var subjectSuccess: StocksRepository
    private lateinit var subjectError: StocksRepository
    private val stocksDao = object : StocksDao {
        override fun flowAllSymbols(): Flow<List<StockEntity>> = flowOf(listOf())
        override suspend fun insert(stock: StockEntity) {}
        override suspend fun deleteBySymbol(stockSymbol: String) {}
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

        private val throwableError = Exception("Something happened")
    }


    @Before
    fun init() {
        subjectSuccess = StocksRepositoryImpl(
            object : YahooFinanceNetworkDataSource {
                override suspend fun search(query: String): List<NetworkStock> {
                    return networkStockList
                }
            }, stocksDao
        )

        subjectError = StocksRepositoryImpl(
            object : YahooFinanceNetworkDataSource {
                override suspend fun search(query: String): List<NetworkStock> {
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
}