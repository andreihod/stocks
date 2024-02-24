package com.andreih.stocks.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andreih.stocks.commom.Result
import com.andreih.stocks.data.model.Stock
import com.andreih.stocks.data.model.StockExchangeName
import com.andreih.stocks.data.model.StockExchangeSymbol
import com.andreih.stocks.data.model.StockLongName
import com.andreih.stocks.data.model.StockName
import com.andreih.stocks.data.model.StockSectorName
import com.andreih.stocks.data.model.StockSymbol
import com.andreih.stocks.ui.theme.StocksTheme
import com.andreih.stocks.ui.viewmodel.SearchViewModel

@Composable
fun SearchScreen(viewModel: SearchViewModel = viewModel()) {
    val searchStocks by viewModel.searchStocksFlow.collectAsStateWithLifecycle()
    val watchedSymbols by viewModel.flowWatchedSymbols.collectAsStateWithLifecycle()

    SearchScreen(
        query = viewModel.query,
        watchedSymbols = watchedSymbols,
        searchStocksResult = searchStocks,
        onSymbolClicked = viewModel::updateSymbolState,
        onQueryChanged = viewModel::updateQuery
    )
}

@Composable
private fun SearchScreen(
    query: String,
    watchedSymbols: List<StockSymbol>,
    searchStocksResult: Result<List<Stock>>,
    onSymbolClicked: (StockSymbol) -> Unit,
    onQueryChanged: (String) -> Unit
) {
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SearchBox(query, onQueryChanged)
        StockSearchList(watchedSymbols, searchStocksResult, onSymbolClicked)
    }
}

@Composable
fun StockSearchList(
    watchedSymbols: List<StockSymbol>,
    searchStocksResult: Result<List<Stock>>,
    onSymbolClicked: (StockSymbol) -> Unit
) {
    Box {
        when (searchStocksResult) {
            is Result.Success -> {
                if (searchStocksResult.data.isEmpty()) {
                    Column(
                        Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxSize(),
                        Arrangement.Center,
                        Alignment.CenterHorizontally
                    ) {
                        Text(
                            "You can search by name or ticker, for example: ",
                            color = MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "\"AAPL\", \"Apple Inc.\", \"Tesla\", \"TSLA\"",
                            color = MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn {
                        items(searchStocksResult.data, key = { it.key }) {
                            val hasSymbolWatched by remember(watchedSymbols) {
                                derivedStateOf {
                                    watchedSymbols.contains(it.symbol)
                                }
                            }

                            StockSearchItem(it, hasSymbolWatched, onSymbolClicked)
                        }
                    }
                }
            }
            is Result.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is Result.Error -> {}
        }
    }
}

@Composable
fun LazyItemScope.StockSearchItem(
    stock: Stock,
    hasSymbolWatched: Boolean,
    onSymbolClicked: (StockSymbol) -> Unit
) {
    Row(
        Modifier
            .clickable { onSymbolClicked(stock.symbol) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier
                .weight(1.0f, fill = true)
                .animateItemPlacement()
        ) {
            Text(stock.symbol.value, fontSize = 20.sp)
            Text(
                stock.name.value,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                stock.exchangeName.value,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }

        AnimatedContent(
            targetState = hasSymbolWatched,
            transitionSpec = {
                if (targetState) {
                    slideInVertically { height -> height } + fadeIn() with
                            slideOutVertically { height -> -height } + fadeOut()
                } else {
                    slideInVertically { height -> -height } + fadeIn() with
                            slideOutVertically { height -> height } + fadeOut()
                }
            }
        ) { isWatched ->
            Icon(
                if (isWatched) Icons.Filled.Remove else Icons.Filled.Add,
                contentDescription = if (isWatched) "Remove" else "Add",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun SearchBox(query: String, onQueryChanged: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search a stock", style = MaterialTheme.typography.titleMedium) },
        maxLines = 1,
        singleLine = true,
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
        shape = RoundedCornerShape(100.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    StocksTheme(false) {
        var query by remember { mutableStateOf("") }
        val watchedSymbols = remember { mutableStateListOf(StockSymbol("PBR")) }

        val searchStocksResult = Result.Success(
            listOf(
                Stock(
                    name = StockName("Petroleo Brasileiro S.A.- Petro"),
                    longName = StockLongName("Petróleo Brasileiro S.A. - Petrobras"),
                    symbol = StockSymbol("PBR"),
                    exchangeSymbol = StockExchangeSymbol("NYQ"),
                    exchangeName = StockExchangeName("NYSE"),
                    sector = StockSectorName("Energy"),
                )
            )
        )

        Surface(modifier = Modifier.fillMaxSize()) {
            SearchScreen(
                query = query,
                watchedSymbols = watchedSymbols,
                searchStocksResult = searchStocksResult,
                onSymbolClicked = {
                    if (watchedSymbols.contains(it))
                        watchedSymbols.remove(it)
                    else
                        watchedSymbols.add(it)
                },
                onQueryChanged = { query = it }
            )
        }
    }
}