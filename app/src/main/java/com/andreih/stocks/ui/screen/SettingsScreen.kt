package com.andreih.stocks.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andreih.stocks.data.model.AppTheme
import com.andreih.stocks.ui.theme.StocksTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(appTheme: AppTheme, onAppThemeChanged: (AppTheme) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { padding ->
        Surface(
            Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Theme", style = MaterialTheme.typography.titleLarge)

                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onAppThemeChanged(AppTheme.Light) }
                        .padding(vertical = 10.dp, horizontal = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RadioButton(selected = appTheme == AppTheme.Light, onClick = null)
                    Text("Light", style = MaterialTheme.typography.titleMedium)
                }

                Row(Modifier
                    .fillMaxWidth()
                    .clickable { onAppThemeChanged(AppTheme.Dark) }
                    .padding(vertical = 10.dp, horizontal = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RadioButton(selected = appTheme == AppTheme.Dark, onClick = null)
                    Text("Dark", style = MaterialTheme.typography.titleMedium)
                }

                Row(Modifier
                    .fillMaxWidth()
                    .clickable { onAppThemeChanged(AppTheme.System) }
                    .padding(vertical = 10.dp, horizontal = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RadioButton(selected = appTheme == AppTheme.System, onClick = null)
                    Text("System", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    StocksTheme {
        SettingsScreen(appTheme = AppTheme.Dark) { }
    }
}