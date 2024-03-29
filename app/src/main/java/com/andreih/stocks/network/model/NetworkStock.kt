package com.andreih.stocks.network.model

import com.andreih.stocks.commom.removeRepeatedWhiteSpaces
import com.andreih.stocks.data.model.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkStock(
    @SerialName("shortname")
    val name: String,
    @SerialName("longname")
    val longName: String?,
    val symbol: String,
    val score: Double,
    @SerialName("exchange")
    val exchangeSymbol: String,
    @SerialName("exchDisp")
    val exchangeName: String,
    @SerialName("sector")
    val sector: String?
)

fun NetworkStock.intoStock(): Stock =
    Stock(
        name = StockName(name.removeRepeatedWhiteSpaces()),
        longName = longName?.let { StockLongName(it.removeRepeatedWhiteSpaces()) },
        symbol = StockSymbol(symbol),
        exchangeSymbol = StockExchangeSymbol(exchangeSymbol),
        exchangeName = StockExchangeName(exchangeName),
        sector = sector?.let { StockSectorName(it) }
)