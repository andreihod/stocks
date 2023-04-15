package com.andreih.stocks.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andreih.stocks.data.model.*
import com.andreih.stocks.ui.theme.StocksTheme
import com.andreih.stocks.ui.theme.success
import com.andreih.stocks.ui.viewmodel.StocksViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder

@Composable
fun StocksScreen(viewModel: StocksViewModel = viewModel()) {
    val isRefreshing = viewModel.isRefreshing
    val quotes by viewModel.quotes.collectAsStateWithLifecycle()

    quotes?.let {
        StocksScreen(
            quotes = it.toList(),
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            onRemove = { symbol -> viewModel.removeSymbol(symbol) }
        )
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
@Composable
private fun StocksScreen(
    quotes: List<Pair<StockSymbol, StockQuote?>>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onRemove: (StockSymbol) -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Stocks") })
        }
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            if (quotes.isEmpty()) {
                Column(
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize(),
                    Arrangement.Center,
                    Alignment.CenterHorizontally
                ) {
                    Text(
                        "You're not following any stocks :(",
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text("Consider searching one",
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(quotes, key = { (symbol, _) -> symbol.value }) { item ->
                        StockItem(
                            item,
                            modifier = Modifier.animateItemPlacement(),
                            onRemove = onRemove
                        )
                    }
                }

                PullRefreshIndicator(
                    isRefreshing,
                    pullRefreshState,
                    Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun StockItem(
    item: Pair<StockSymbol, StockQuote?>,
    onRemove: (StockSymbol) -> Unit,
    modifier: Modifier = Modifier
) {
    val (symbol, quote) = item

    SwipeToDismiss(
        state = rememberDismissState(
            confirmStateChange = {
                if (it == DismissValue.DismissedToStart)
                    onRemove(symbol)
                true
            }
        ),
        dismissThresholds = { FractionalThreshold(0.3f) },
        directions = setOf(DismissDirection.EndToStart),
        modifier = modifier,
        background = {
            Row(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Delete")
                Icon(
                    Icons.Filled.Delete,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(end = 20.dp),
                    contentDescription = "Remove"
                )
            }
        },
        dismissContent = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    Modifier.weight(1f, fill = true),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(symbol.value)
                    Text(
                        quote?.stockName?.value ?: "Stock Name Placeholder",
                        Modifier.placeholder(
                            visible = (quote?.stockName == null),
                            highlight = PlaceholderHighlight.fade()
                        ),
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
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

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
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
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewStocksScreen() {
    StocksTheme(false) {
        val quotes = listOf(
            Pair(
                StockSymbol("AAPL"),
                StockQuote(
                    symbol = StockSymbol("AAPL"),
                    stockName = StockName("Apple"),
                    stockLongName = StockLongName("Apple Inc."),
                    currency = StockQuoteCurrency("USD"),
                    timezone = StockQuoteTimezone("New_York/US"),
                    marketChange = 1.2,
                    marketChangePercent = 0.023,
                    marketPrice = 102.20,
                    marketDayHigh = 103.1,
                    marketDayLow = 101.98,
                    marketVolume = 329292.02,
                    marketPreviousClose = 100.21,
                    marketState = StockQuoteMarketState("OPEN")
                )
            ),
            Pair(
                StockSymbol("GOOG"),
                StockQuote(
                    symbol = StockSymbol("GOOG"),
                    stockName = StockName("Google Very Loooooooooooooooooong Name"),
                    stockLongName = StockLongName("Google Inc."),
                    currency = StockQuoteCurrency("USD"),
                    timezone = StockQuoteTimezone("New_York/US"),
                    marketChange = 1.2,
                    marketChangePercent = 0.023,
                    marketPrice = 102.20,
                    marketDayHigh = 103.1,
                    marketDayLow = 101.98,
                    marketVolume = 329292.02,
                    marketPreviousClose = 100.21,
                    marketState = StockQuoteMarketState("OPEN")
                )
            ),
            Pair(StockSymbol("AMZN"), null)
        )

        StocksScreen(quotes = quotes, isRefreshing = false, onRefresh = {}, onRemove = {})
    }
}