package com.andreih.stocks.network.model

import com.andreih.stocks.data.model.Stock
import kotlinx.serialization.Serializable

@Serializable
data class NetworkStock(
    val symbol: String,
    val name: String,
    val type: String,
    val region: String,
    val marketOpen: String,
    val marketClose: String,
    val timezone: String,
    val currency: String
)

fun NetworkStock.intoStock(): Stock =
    Stock(
        symbol = symbol,
        name = name,
        type = type,
        region = region,
        marketOpen = marketOpen,
        marketClose = marketClose,
        timezone = timezone,
        currency = currency
    )
