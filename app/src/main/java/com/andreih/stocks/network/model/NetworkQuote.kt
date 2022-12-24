package com.andreih.stocks.network.model

import com.andreih.stocks.data.model.StockQuote
import kotlinx.serialization.Serializable

@Serializable
data class NetworkQuote(
    val symbol: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val price: Double,
    val volume: Double,
    val change: Double,
    val changePercent: String,
)

fun NetworkQuote.intoStockQuote(): StockQuote =
    StockQuote(
        symbol = symbol,
        open = open,
        high = high,
        low = low,
        price = price,
        volume = volume,
        change = change,
        changePercent = changePercent,
    )
