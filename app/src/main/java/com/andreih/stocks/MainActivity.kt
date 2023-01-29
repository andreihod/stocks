package com.andreih.stocks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import com.andreih.stocks.ui.StocksApp
import com.andreih.stocks.ui.screen.SearchScreen
import com.andreih.stocks.ui.theme.StocksTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StocksTheme {
                StocksApp(
                    stocksScreen = {
                        // TO-DO Stocks screen
                    },
                    searchScreen = {
                        SearchScreen(hiltViewModel())
                    },
                    settingsScreen = {
                        // TO-DO Settings screen
                    }
                )
            }
        }
    }
}
