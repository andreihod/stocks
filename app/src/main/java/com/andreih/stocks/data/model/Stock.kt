package com.andreih.stocks.data.model

@JvmInline
value class StockName(val value: String)

@JvmInline
value class StockLongName(val value: String)

@JvmInline
value class StockSymbol(val value: String)

@JvmInline
value class StockExchangeSymbol(val value: String)

@JvmInline
value class StockExchangeName(val value: String)

@JvmInline
value class StockSectorName(val value: String)

data class Stock(
    val name: StockName,
    val longName: StockLongName?,
    val symbol: StockSymbol,
    val exchangeSymbol: StockExchangeSymbol,
    val exchangeName: StockExchangeName,
    val sector: StockSectorName?
) {
    val key = "${symbol.value}/${exchangeSymbol.value}"
}