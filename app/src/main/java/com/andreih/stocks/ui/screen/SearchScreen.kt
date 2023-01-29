package com.andreih.stocks.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andreih.stocks.commom.Result
import com.andreih.stocks.data.model.*
import com.andreih.stocks.ui.theme.StocksTheme
import com.andreih.stocks.ui.viewmodel.SearchViewModel

@Composable
fun SearchScreen(viewModel: SearchViewModel = viewModel()) {
    val searchStocks by viewModel.searchStocksFlow.collectAsStateWithLifecycle()

    SearchScreen(viewModel.query, searchStocks) {
        viewModel.updateQuery(it)
    }
}

@Composable
private fun SearchScreen(
    query: String,
    searchStocksResult: Result<List<Stock>>,
    onQueryChanged: (String) -> Unit
) {
    var isSearching by remember { mutableStateOf(false) }

    Column {
        SearchBox(query, { isSearching = it }, onQueryChanged)

        AnimatedVisibility(
            visible = isSearching,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            Divider(
                Modifier.padding(0.dp, 16.dp),
                color = MaterialTheme.colorScheme.onBackground,
                thickness = 1.dp
            )
        }

        Box(Modifier.fillMaxSize()) {
            StockSearchList(searchStocksResult)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StockSearchList(searchStocksResult: Result<List<Stock>>) {
    Box {
        when (searchStocksResult) {
            is Result.Success -> {
                LazyColumn {
                    items(searchStocksResult.data, key = { it.key }) {
                        Column(
                            Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .animateItemPlacement()
                        ) {
                            Text(it.symbol.value, fontSize = 20.sp)
                            Text(
                                it.name.value,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBox(query: String, onFocusChanged: (Boolean) -> Unit, onQueryChanged: (String) -> Unit) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(query) {
        if (query.isEmpty()) {
            focusManager.clearFocus()
        }
    }

    OutlinedTextField(
        value = query,
        placeholder = { Text("Search a stock") },
        maxLines = 1,
        singleLine = true,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { onFocusChanged(it.isFocused) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
        shape = RoundedCornerShape(100.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    StocksTheme(false) {
        var query by remember { mutableStateOf("") }
        val searchStocksResult = Result.Success(
            listOf(
                Stock(
                    name = StockName("Petrobras S.A."),
                    symbol = StockSymbol("PETR4.SA"),
                    stockExchange = StockExchange(
                        name = StockExchangeName("Bovespa"),
                        acronym = StockExchangeAcronym("BVSP"),
                        micId = StockExchangeMicId("BVSP"),
                        countryCode = StockExchangeCountryCode("BR"),
                        country = StockExchangeCountry("Brazil"),
                        city = StockExchangeCity("São Paulo")
                    )
                )
            )
        )

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(Modifier.padding(16.dp)) {
                SearchScreen(query, searchStocksResult) {
                    query = it
                }
            }
        }
    }
}