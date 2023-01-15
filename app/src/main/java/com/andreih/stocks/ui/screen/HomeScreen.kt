package com.andreih.stocks.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andreih.stocks.commom.Result
import com.andreih.stocks.data.model.*
import com.andreih.stocks.ui.theme.StocksTheme

@Composable
fun HomeScreen(
    query: String,
    searchStocksResult: Result<List<Stock>>,
    onSearchFocusChanged: (Boolean) -> Unit,
    onQueryChanged: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    SearchBox(query, onSearchFocusChanged, onQueryChanged)

    Box(
        Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { focusManager.clearFocus() }) {
        StockSearchList(searchStocksResult)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StockSearchList(searchStocksResult: Result<List<Stock>>) {
    Box(Modifier.padding(0.dp, 16.dp)) {
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
        shape = RoundedCornerShape(100.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
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
            Column(Modifier.background(MaterialTheme.colorScheme.secondaryContainer).padding(16.dp)) {
                HomeScreen(query, searchStocksResult, { }) {
                    query = it
                }
            }
        }
    }
}