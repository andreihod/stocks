package com.andreih.stocks.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.andreih.stocks.data.dao.StocksDao
import com.andreih.stocks.data.entity.StockEntity
import com.andreih.stocks.data.entity.StockQuoteEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

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
    fun insertsAndRemoveCorrectly() = runTest {
        val stockA = StockEntity(0, "GOOG")
        val stockB = StockEntity(0, "IBM")

        stocksDao.insertStock(stockA)
        assertEquals(listOf(StockEntity(1, "GOOG")), stocksDao.listStocks())

        stocksDao.insertStock(stockB)
        assertEquals(
            listOf(
                StockEntity(1, "GOOG"),
                StockEntity(2, "IBM")
            ),
            stocksDao.listStocks()
        )

        stocksDao.deleteStockBySymbol("GOOG")
        assertEquals(listOf(StockEntity(2, "IBM")), stocksDao.listStocks())
    }

    @Test
    fun insertsTwoIdenticalStocksDoesNotThrowException() = runTest {
        try {
            val stockA = StockEntity(0, "GOOG")
            val stockB = StockEntity(0, "GOOG")
            stocksDao.insertStock(stockA)
            stocksDao.insertStock(stockB)
        } catch (t: Throwable) {
            fail("Expected to not thrown $t")
        }
    }

    @Test
    fun insertOrReplaceStockQuotes() = runTest {
        assertEquals(listOf<StockQuoteEntity>(), stocksDao.allQuotes(listOf()))

        val quoteA = StockQuoteEntity(
            symbol = "GOOG",
            stockName = "Google",
            stockLongName = "Google Inc.",
            currency = "USD",
            timezone = "NewYork/USA",
            marketChange = 1.0,
            marketChangePercent = 0.12,
            marketPrice = 100.1,
            marketDayHigh = 101.2,
            marketDayLow = 99.8,
            marketPreviousClose = 99.9,
            marketVolume = 1000300.0,
            marketState = "OPEN"
        )

        stocksDao.insertQuotes(listOf(quoteA))
        assertEquals(listOf(quoteA.copy(uid = 1)), stocksDao.allQuotes(listOf("GOOG")))

        val updatedQuoteA = quoteA.copy(marketDayHigh = 103.5)
        val quoteB = StockQuoteEntity(
            symbol = "AMZN",
            stockName = "Amazon",
            stockLongName = "Amazon Inc.",
            currency = "USD",
            timezone = "NewYork/USA",
            marketChange = 1.0,
            marketChangePercent = 0.12,
            marketPrice = 100.1,
            marketDayHigh = 101.2,
            marketDayLow = 99.8,
            marketPreviousClose = 99.9,
            marketVolume = 1000300.0,
            marketState = "OPEN"
        )

        stocksDao.insertQuotes(listOf(updatedQuoteA, quoteB))
        val expectedQuotes = listOf(updatedQuoteA.copy(uid = 2), quoteB.copy(uid = 3))
        assertEquals(expectedQuotes, stocksDao.allQuotes(listOf("GOOG", "AMZN")))
    }
}