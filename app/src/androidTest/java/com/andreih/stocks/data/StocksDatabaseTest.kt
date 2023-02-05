package com.andreih.stocks.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.andreih.stocks.data.dao.StocksDao
import com.andreih.stocks.data.entity.StockEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class StocksDatabaseTest {
    private lateinit var stocksDao: StocksDao
    private lateinit var db: StocksDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, StocksDatabase::class.java).build()
        stocksDao = db.stockDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertsAndFlowsAccordingly() = runTest {
        stocksDao.flowAllSymbols().distinctUntilChanged().test {
            val stockA = StockEntity(0, "GOOG")
            val stockB = StockEntity(0, "IBM")

            stocksDao.insert(stockA)
            assertEquals(listOf(StockEntity(1, "GOOG")), awaitItem())

            stocksDao.insert(stockB)
            assertEquals(
                listOf(
                    StockEntity(1, "GOOG"),
                    StockEntity(2, "IBM")
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun deletesAndFlowsAccordingly() = runTest {
        val stockA = StockEntity(0, "GOOG")
        val stockB = StockEntity(0, "IBM")
        stocksDao.insert(stockA)
        stocksDao.insert(stockB)

        stocksDao.flowAllSymbols().distinctUntilChanged().test {
            stocksDao.deleteBySymbol("GOOG")
            assertEquals(listOf(StockEntity(2, "IBM")), awaitItem())
        }
    }

    @Test
    fun insertsTwoIdenticalStocksDoesNotThrowException() = runTest {
        try {
            val stockA = StockEntity(0, "GOOG")
            val stockB = StockEntity(0, "GOOG")
            stocksDao.insert(stockA)
            stocksDao.insert(stockB)
        } catch (t: Throwable) {
            fail("Expected to not thrown $t")
        }
    }
}