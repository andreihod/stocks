package com.andreih.stocks.data.model

@JvmInline
value class StockName(val value: String)

@JvmInline
value class StockSymbol(val value: String)

data class Stock(
    val name: StockName,
    val symbol: StockSymbol,
    val stockExchange: StockExchange
)

@JvmInline
value class StockExchangeName(val value: String)

@JvmInline
value class StockExchangeAcronym(val value: String)

@JvmInline
value class StockExchangeMicId(val value: String)

@JvmInline
value class StockExchangeCountry(val value: String)

@JvmInline
value class StockExchangeCountryCode(val value: String)

@JvmInline
value class StockExchangeCity(val value: String)

data class StockExchange(
    val name: StockExchangeName,
    val acronym: StockExchangeAcronym?,
    val micId: StockExchangeMicId,
    val country: StockExchangeCountry?,
    val countryCode: StockExchangeCountryCode,
    val city: StockExchangeCity?
)