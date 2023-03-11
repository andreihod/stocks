package com.andreih.stocks.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.andreih.stocks.data.model.*

@Entity(tableName = "quotes", indices = [Index(value = ["stock_symbol"], unique = true)])
data class StockQuoteEntity(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    @ColumnInfo(name = "stock_symbol")
    val symbol: String,
    @ColumnInfo(name = "stock_name")
    val stockName: String,
    @ColumnInfo(name = "stock_long_name")
    val stockLongName: String,
    val currency: String,
    val timezone: String,
    @ColumnInfo(name = "market_change")
    val marketChange: Double,
    @ColumnInfo(name = "market_change_percent")
    val marketChangePercent: Double,
    @ColumnInfo(name = "market_price")
    val marketPrice: Double,
    @ColumnInfo(name = "market_day_high")
    val marketDayHigh: Double,
    @ColumnInfo(name = "market_day_low")
    val marketDayLow: Double,
    @ColumnInfo(name = "market_volume")
    val marketVolume: Double,
    @ColumnInfo(name = "market_previous_close")
    val marketPreviousClose: Double,
    @ColumnInfo(name = "market_state")
    val marketState: String
)