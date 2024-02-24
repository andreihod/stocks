package com.andreih.stocks.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreih.stocks.commom.Result
import com.andreih.stocks.data.model.StockSymbol
import com.andreih.stocks.data.repository.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val stocksRepository: StocksRepository
) : ViewModel() {
    private sealed class SearchInput {
        data object Empty : SearchInput()
        data class Query(val query: String) : SearchInput()
    }

    var query by mutableStateOf("")
        private set

    val flowWatchedSymbols = stocksRepository
        .flowWatchedSymbols()
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            listOf()
        )

    val searchStocksFlow = snapshotFlow { query }
        .map(::mapQueryToSearchInput)
        .debounce(500L)
        .conflate()
        .map(::handleSearchInput)
        .flattenConcat()
        .onEach { println(it) }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            Result.Success(listOf())
        )

    fun updateQuery(newQuery: String) {
        query = newQuery
    }

    fun updateSymbolState(symbol: StockSymbol) {
        viewModelScope.launch {
            if (flowWatchedSymbols.value.contains(symbol)) {
                stocksRepository.unwatchSymbol(symbol)
            } else {
                stocksRepository.watchSymbol(symbol)
            }
        }
    }

    private fun mapQueryToSearchInput(query: String) =
        when {
            query.length < 3 -> SearchInput.Empty
            else -> SearchInput.Query(query)
        }

    private suspend fun handleSearchInput(input: SearchInput) =
        when (input) {
            SearchInput.Empty -> flowOf(Result.Success(listOf()))
            is SearchInput.Query -> stocksRepository.search(input.query)
        }
}