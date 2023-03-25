package com.andreih.stocks.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreih.stocks.commom.Result
import com.andreih.stocks.data.model.StockQuote
import com.andreih.stocks.data.model.StockSymbol
import com.andreih.stocks.data.repository.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class StocksViewModel @Inject constructor(
    private val stocksRepository: StocksRepository
) : ViewModel() {
    private val refreshingFlow = snapshotFlow { isRefreshing }.filter { it }

    var isRefreshing by mutableStateOf(false)
        private set

    private val symbolsFlow = stocksRepository
        .flowWatchedSymbols()
        // Emit the latest symbol and listen to refresh updates
        .transformLatest { symbols -> emit(symbols); refreshingFlow.collect { emit(symbols) } }
        .filter { it.isNotEmpty() }

    val quotes = symbolsFlow
        .conflate()
        // Keep updating the flow every 30s
        .transformLatest { while (true) { emit(it); delay(30_000) } }
        .flatMapConcat(::fetchRemoteQuotes)
        .stateIn(viewModelScope, SharingStarted.Lazily, mapOf())

    fun refresh() {
        isRefreshing = true
    }

    private suspend fun fetchRemoteQuotes(symbols: List<StockSymbol>) =
        stocksRepository.remoteQuotes(symbols)
            // When the request stops loading, mark the refreshing as finished
            .filter { it !is Result.Loading }.onEach { isRefreshing = false }
            .filterIsInstance<Result.Success<List<StockQuote>>>()
            .onEach(::cacheRemoteQuotes)
            .map(::associateRemoteQuotes)
            .onStart { emit(fetchLocalQuotes(symbols)) }

    private suspend fun cacheRemoteQuotes(result: Result.Success<List<StockQuote>>) {
        stocksRepository.saveRemoteQuotes(result.data)
    }

    private fun associateRemoteQuotes(result: Result.Success<List<StockQuote>>): Map<StockSymbol, StockQuote?> {
        return result.data.associateBy(StockQuote::symbol)
    }

    private suspend fun fetchLocalQuotes(symbols: List<StockSymbol>): Map<StockSymbol, StockQuote?> =
        symbols.associateWith { null } + stocksRepository.localQuotes(symbols)
            .associateBy(StockQuote::symbol)
}
