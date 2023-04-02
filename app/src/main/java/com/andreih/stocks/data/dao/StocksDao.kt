package com.andreih.stocks.data.dao

import androidx.room.*
import com.andreih.stocks.data.entity.StockEntity
import com.andreih.stocks.data.entity.StockQuoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StocksDao {
    @Query("SELECT uid, stock_symbol FROM stocks ORDER BY uid")
    suspend fun listStocks(): List<StockEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStock(stock: StockEntity)

    @Query("DELETE FROM stocks WHERE stock_symbol = :stockSymbol")
    suspend fun deleteStockBySymbol(stockSymbol: String)

    @Query("SELECT * FROM quotes WHERE stock_symbol in (:stockSymbols) ORDER BY uid")
    suspend fun allQuotes(stockSymbols: List<String>): List<StockQuoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<StockQuoteEntity>)
}