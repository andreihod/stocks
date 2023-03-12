package com.andreih.stocks.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andreih.stocks.data.model.StockQuote
import com.andreih.stocks.ui.viewmodel.StocksViewModel
import com.andreih.stocks.data.model.StockSymbol
import com.andreih.stocks.ui.theme.StocksTheme
import com.andreih.stocks.ui.theme.success
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder

@Composable
fun StocksScreen(viewModel: StocksViewModel = viewModel()) {
    val quotes by viewModel.quotes.collectAsStateWithLifecycle()

    StocksScreen(quotes.toList())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StocksScreen(quotes: List<Pair<StockSymbol, StockQuote?>>) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Stocks") })
        }
    ) {
        LazyColumn(
            Modifier
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            items(quotes, key = { (symbol, _) -> symbol.value }) { (symbol, quote) ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(Modifier.weight(2f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(symbol.value)
                        Text(
                            quote?.stockName?.value ?: "Stock Name Placeholder",
                            Modifier.placeholder(
                                visible = (quote?.stockName == null),
                                highlight = PlaceholderHighlight.fade()
                            ),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            quote?.marketPriceFormatted ?: "Price",
                            Modifier.placeholder(
                                visible = (quote?.marketPrice == null),
                                highlight = PlaceholderHighlight.fade(),
                            )
                        )
                        Text(
                            quote?.marketPreviousCloseFormatted ?: "Prev. 100",
                            Modifier.placeholder(
                                visible = (quote?.marketPreviousClose == null),
                                highlight = PlaceholderHighlight.fade()
                            ),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Column(Modifier.weight(0.6f), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            quote?.marketChangePercentFormatted ?: "Percent",
                            Modifier.placeholder(
                                visible = (quote?.marketChangePercent == null),
                                highlight = PlaceholderHighlight.fade()
                            ),
                            color = when {
                                quote == null -> MaterialTheme.colorScheme.onBackground
                                quote.marketChangePercent >= 0 -> MaterialTheme.colorScheme.success
                                quote.marketChangePercent < 0 -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onBackground
                            }
                        )
                        Text(
                            quote?.marketChangeFormatted ?: "Change",
                            Modifier.placeholder(
                                visible = (quote?.marketPrice == null),
                                highlight = PlaceholderHighlight.fade(),
                            ),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewStocksScreen() {
    StocksTheme(false) {
        val quotes = listOf(
            Pair(StockSymbol("AAPL"), null),
            Pair(StockSymbol("GOOG"), null)
        )

        StocksScreen(quotes = quotes)
    }
}