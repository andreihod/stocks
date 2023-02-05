package com.andreih.stocks.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "stocks", indices = [Index(value = ["stock_symbol"], unique = true)])
data class StockEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "stock_symbol") val symbol: String,
)