package com.andreih.stocks.network.model

import com.andreih.stocks.data.model.*
import kotlinx.serialization.Serializable

@Serializable
data class NetworkQuote(
    val symbol: String,
    val shortName: String,
    val longName: String,
    val currency: String,
    val exchangeTimezoneName: String,
    val regularMarketChange: Double,
    val regularMarketChangePercent: Double,
    val regularMarketPrice: Double,
    val regularMarketDayHigh: Double,
    val regularMarketDayLow: Double,
    val regularMarketVolume: Double,
    val regularMarketPreviousClose: Double,
    val marketState: String
)

fun NetworkQuote.intoStockQuote(): StockQuote =
    StockQuote(
        symbol = StockSymbol(symbol),
        stockName = StockName(shortName),
        stockLongName = StockLongName(shortName),
        currency = StockQuoteCurrency(currency),
        timezone = StockQuoteTimezone(exchangeTimezoneName),
        marketChange = regularMarketChange,
        marketChangePercent= regularMarketChangePercent,
        marketPrice = regularMarketPrice,
        marketDayHigh = regularMarketDayHigh,
        marketDayLow = regularMarketDayLow,
        marketVolume = regularMarketVolume,
        marketPreviousClose = regularMarketPreviousClose,
        marketState = StockQuoteMarketState(marketState)
    )
