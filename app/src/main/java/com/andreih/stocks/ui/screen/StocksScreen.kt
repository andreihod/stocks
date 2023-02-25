package com.andreih.stocks.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andreih.stocks.data.model.StockQuote
import com.andreih.stocks.ui.viewmodel.StocksViewModel
import com.andreih.stocks.data.model.StockSymbol
import com.andreih.stocks.ui.theme.StocksTheme
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder

@Composable
fun StocksScreen(viewModel: StocksViewModel = viewModel()) {
    val quotes by viewModel.quotes.collectAsStateWithLifecycle()

    StocksScreen(quotes.toList())
}

@Composable
private fun StocksScreen(quotes: List<Pair<StockSymbol, StockQuote?>>) {
    Column(verticalArrangement = Arrangement.spacedBy(42.dp)) {
        Text("Portfolio", fontSize = 26.sp, fontWeight = FontWeight.Bold)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(28.dp)) {
            items(quotes, key = { (symbol, _) -> symbol.value }) { (symbol, quote) ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(symbol.value)
                        Text(
                            quote?.marketPriceFormatted ?: "Price",
                            Modifier.placeholder(
                                visible = (quote?.marketPrice == null),
                                highlight = PlaceholderHighlight.fade(),
                            )
                        )
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            quote?.stockName?.value ?: "Stock Name Placeholder",
                            Modifier.placeholder(
                                visible = (quote?.stockName == null),
                                highlight = PlaceholderHighlight.fade()
                            ),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.outline
                        )

                        Text(
                            quote?.marketChangeFormatted ?: "Change",
                            Modifier.placeholder(
                                visible = (quote?.marketChange == null),
                                highlight = PlaceholderHighlight.fade()
                            )
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