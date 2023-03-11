package com.andreih.stocks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreih.stocks.data.repository.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import com.andreih.stocks.commom.Result
import com.andreih.stocks.data.model.StockQuote
import com.andreih.stocks.data.model.StockSymbol
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
@HiltViewModel
class StocksViewModel @Inject constructor(
    private val stocksRepository: StocksRepository
) : ViewModel() {
    val quotes = stocksRepository
        .flowWatchedSymbols()
        .conflate()
        .flatMapConcat(::fetchRemoteQuotes)
        .stateIn(viewModelScope, SharingStarted.Lazily, mapOf())

    private suspend fun fetchRemoteQuotes(symbols: List<StockSymbol>) =
        stocksRepository.remoteQuotes(symbols)
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
        symbols.associateWith { null } + stocksRepository.localQuotes(symbols).associateBy(StockQuote::symbol)
}
