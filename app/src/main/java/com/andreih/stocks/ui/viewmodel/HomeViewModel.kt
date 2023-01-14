package com.andreih.stocks.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.andreih.stocks.commom.Result
import com.andreih.stocks.data.repository.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val stocksRepository: StocksRepository
) : ViewModel() {
    private sealed class SearchInput {
        object Empty : SearchInput()
        data class Query(val query: String) : SearchInput()
    }

    var query by mutableStateOf("")
        private set

    val searchStocksFlow = snapshotFlow { query }
        .map(::mapQueryToSearchInput)
        .debounce(500L)
        .conflate()
        .map(::handleSearchInput)
        .flattenConcat()

    fun updateQuery(newQuery: String) {
        query = newQuery
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