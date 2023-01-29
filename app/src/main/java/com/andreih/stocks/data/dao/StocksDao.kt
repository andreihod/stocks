package com.andreih.stocks.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.andreih.stocks.data.entity.StockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StocksDao {
    @Query("SELECT uid, stock_symbol FROM stocks")
    fun streamStocks(): Flow<List<StockEntity>>

    @Insert
    suspend fun insert(stock: StockEntity)

    @Query("DELETE FROM stocks WHERE stock_symbol = :stockSymbol")
    suspend fun deleteBySymbol(stockSymbol: String)
}