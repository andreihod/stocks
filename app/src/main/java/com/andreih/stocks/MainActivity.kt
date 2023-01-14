package com.andreih.stocks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(Modifier.padding(16.dp)) {
            HomeScreen(viewModel.query, searchStocks) {
                viewModel.updateQuery(it)
            }
        }
    }
}
