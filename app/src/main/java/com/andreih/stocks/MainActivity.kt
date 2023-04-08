package com.andreih.stocks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.andreih.stocks.data.model.AppTheme
import com.andreih.stocks.data.model.Settings
import com.andreih.stocks.data.repository.SettingsRepository
import com.andreih.stocks.ui.StocksApp
import com.andreih.stocks.ui.screen.SearchScreen
import com.andreih.stocks.ui.screen.SettingsScreen
import com.andreih.stocks.ui.screen.StocksScreen
import com.andreih.stocks.ui.theme.StocksTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val settings by settingsRepository.settings.collectAsState(Settings.default)

            StocksTheme(
                darkTheme = when (settings.appTheme) {
                    AppTheme.Light -> false
                    AppTheme.Dark -> true
                    AppTheme.System -> isSystemInDarkTheme()
                }
            ) {
                val scope = rememberCoroutineScope()

                StocksApp(
                    stocksScreen = {
                        StocksScreen(hiltViewModel())
                    },
                    searchScreen = {
                        SearchScreen(hiltViewModel())
                    },
                    settingsScreen = {
                        SettingsScreen(settings.appTheme) {
                            scope.launch { settingsRepository.setAppTheme(it) }
                        }
                    }
                )
            }
        }
    }
}
