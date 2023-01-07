package com.andreih.stocks.network.model

import com.andreih.stocks.data.model.*
import kotlinx.datetime.TimeZone
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URL

@Serializable
data class NetworkStock(
    val name: String,
    val symbol: String,
    @SerialName("stock_exchange")
    val stockExchange: NetworkStockExchange
)

@Serializable
data class NetworkStockExchange(
    val name: String,
    val acronym: String?,
    val mic: String,
    val country: String?,
    @SerialName("country_code")
    val countryCode: String,
    val city: String?
)

fun NetworkStock.intoStock(): Stock =
    Stock(
        name = StockName(name),
        symbol = StockSymbol(symbol),
        stockExchange = stockExchange.intoStockExchange()
)

fun NetworkStockExchange.intoStockExchange(): StockExchange =
    StockExchange(
        name = StockExchangeName(name),
        acronym = acronym?.let { StockExchangeAcronym(it) },
        micId = StockExchangeMicId(mic),
        country = country?.let { StockExchangeCountry(it) },
        countryCode = StockExchangeCountryCode(countryCode),
        city = city?.let { StockExchangeCity(it) }
    )