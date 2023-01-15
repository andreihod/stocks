package com.andreih.stocks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andreih.stocks.commom.Result
import com.andreih.stocks.data.model.Stock
import com.andreih.stocks.ui.theme.StocksTheme
import com.andreih.stocks.ui.screen.HomeScreen
import com.andreih.stocks.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StocksTheme(dynamicColor = false) {
                StocksApp()
            }
        }
    }
}

@Composable
fun StocksApp() {
    val viewModel: HomeViewModel = viewModel()
    val initialState = Result.Success(listOf<Stock>())
    val searchStocks by viewModel.searchStocksFlow.collectAsStateWithLifecycle(initialState)

    var isSearching by remember { mutableStateOf(false) }

    val background by animateColorAsState(
        targetValue = if (isSearching)
            MaterialTheme.colorScheme.secondaryContainer
        else
            MaterialTheme.colorScheme.background,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
    )

    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.background(background).padding(16.dp)) {
            HomeScreen(viewModel.query, searchStocks, { isSearching = it }) {
                viewModel.updateQuery(it)
            }
        }
    }
}
