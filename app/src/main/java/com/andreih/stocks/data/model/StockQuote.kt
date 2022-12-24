package com.andreih.stocks.data.model

import java.math.BigDecimal

data class StockQuote(
    val symbol: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val price: Double,
    val volume: Double,
    val change: Double,
    val changePercent: String,
)
