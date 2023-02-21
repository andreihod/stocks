package com.andreih.stocks.data.model

@JvmInline
value class StockQuoteCurrency(val value: String)

@JvmInline
value class StockQuoteTimezone(val value: String)

@JvmInline
value class StockQuoteMarketState(val value: String)

data class StockQuote(
    val symbol: StockSymbol,
    val stockName: StockName,
    val stockLongName: StockLongName,
    val currency: StockQuoteCurrency,
    val timezone: StockQuoteTimezone,
    val marketChange: Double,
    val marketChangePercent: Double,
    val marketPrice: Double,
    val marketDayHigh: Double,
    val marketDayLow: Double,
    val marketVolume: Double,
    val marketPreviousClose: Double,
    val marketState: StockQuoteMarketState
)
