package com.andreih.stocks.data.model

import com.andreih.stocks.data.entity.StockQuoteEntity
import java.text.NumberFormat
import java.util.*

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
) {
    private val percentFormatter = NumberFormat.getPercentInstance().apply {
        maximumFractionDigits = 2
    }
    private val currencyFormatter = NumberFormat.getCurrencyInstance().apply {
        currency = Currency.getInstance(this@StockQuote.currency.value)
    }

    val marketChangePercentFormatted: String get() = percentFormatter.format(marketChangePercent / 100.0)
    val marketChangeFormatted: String get() = currencyFormatter.format(marketChange)
    val marketPriceFormatted: String get() = currencyFormatter.format(marketPrice)
    val marketDayHighFormatted: String get() = currencyFormatter.format(marketDayHigh)
    val marketDayLowFormatted: String get() = currencyFormatter.format(marketDayLow)
    val marketVolumeFormatted: String get() = currencyFormatter.format(marketVolume)
    val marketPreviousCloseFormatted: String get() = "Prev. ${currencyFormatter.format(marketPreviousClose)}"
}

fun StockQuote.intoStockQuoteEntity(): StockQuoteEntity =
    StockQuoteEntity(
        symbol = symbol.value,
        stockName = stockName.value,
        stockLongName = stockLongName.value,
        currency = currency.value,
        timezone = timezone.value,
        marketChange = marketChange,
        marketChangePercent = marketChangePercent,
        marketPrice = marketPrice,
        marketDayHigh = marketDayHigh,
        marketDayLow = marketDayLow,
        marketVolume = marketVolume,
        marketPreviousClose = marketPreviousClose,
        marketState = marketState.value
    )
