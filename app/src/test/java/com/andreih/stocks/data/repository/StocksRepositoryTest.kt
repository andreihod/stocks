package com.andreih.stocks.data.repository

import com.andreih.stocks.data.model.*
import com.andreih.stocks.commom.Result
import com.andreih.stocks.data.dao.StocksDao
import com.andreih.stocks.data.entity.StockEntity
import com.andreih.stocks.network.MarketStackNetworkDataSource
import com.andreih.stocks.network.model.NetworkStock
import com.andreih.stocks.network.model.NetworkStockExchange
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
        override fun streamStocks(): Flow<List<StockEntity>> = flowOf(listOf())
        override suspend fun insert(stock: StockEntity) {}
        override suspend fun deleteBySymbol(stockSymbol: String) {}
    }

    companion object {
        private val networkStockList = listOf(
            NetworkStock(
                name = "ACME Inc.",
                symbol = "ACME",
                stockExchange = NetworkStockExchange(
                    name = "Nasdaq",
                    acronym = "NSDQ",
                    mic = "NSDQ",
                    country = "USA",
                    countryCode = "USA",
                    city = "New York"
                )
            )
        )

        private val expectedStockList = listOf(
            Stock(
                name = StockName("ACME Inc."),
                symbol = StockSymbol("ACME"),
                stockExchange = StockExchange(
                    name = StockExchangeName("Nasdaq"),
                    acronym = StockExchangeAcronym("NSDQ"),
                    micId = StockExchangeMicId("NSDQ"),
                    country = StockExchangeCountry("USA"),
                    countryCode = StockExchangeCountryCode("USA"),
                    city = StockExchangeCity("New York")
                )
            )
        )

        private val throwableError = Exception("Something happened")
    }


    @Before
    fun init() {
        subjectSuccess = StocksRepositoryImpl(
            object : MarketStackNetworkDataSource {
                override suspend fun search(query: String): List<NetworkStock> {
                    return networkStockList
                }
            }, stocksDao
        )

        subjectError = StocksRepositoryImpl(
            object : MarketStackNetworkDataSource {
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