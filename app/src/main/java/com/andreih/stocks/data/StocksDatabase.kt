package com.andreih.stocks.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.andreih.stocks.data.dao.StocksDao
import com.andreih.stocks.data.entity.StockEntity
import com.andreih.stocks.data.entity.StockQuoteEntity

@Database(entities = [StockEntity::class, StockQuoteEntity::class], version = 1)
abstract class StocksDatabase : RoomDatabase() {
    abstract fun stockDao(): StocksDao
}
