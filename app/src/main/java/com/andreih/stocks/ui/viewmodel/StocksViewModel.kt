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
        .flatMapConcat { fetchRemoteQuotes(it).onStart { emit(it.associateWith { null }) } }
        .stateIn(viewModelScope, SharingStarted.Lazily, mapOf())

    private suspend fun fetchRemoteQuotes(symbols: List<StockSymbol>): Flow<Map<StockSymbol, StockQuote?>> =
        stocksRepository.quotes(symbols)
            .filterIsInstance<Result.Success<List<StockQuote>>>()
            .map { it.data.associateBy(StockQuote::symbol) }
}
