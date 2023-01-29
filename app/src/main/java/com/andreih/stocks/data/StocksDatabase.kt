package com.andreih.stocks.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.andreih.stocks.data.dao.StocksDao
import com.andreih.stocks.data.entity.StockEntity

@Database(entities = [StockEntity::class], version = 1)
abstract class StocksDatabase : RoomDatabase() {
    abstract fun stockDao(): StocksDao
}
