package com.andreih.stocks.data.dao

import androidx.room.*
import com.andreih.stocks.data.entity.StockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StocksDao {
    @Query("SELECT uid, stock_symbol FROM stocks ORDER BY uid")
    fun flowAllSymbols(): Flow<List<StockEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(stock: StockEntity)

    @Query("DELETE FROM stocks WHERE stock_symbol = :stockSymbol")
    suspend fun deleteBySymbol(stockSymbol: String)
}