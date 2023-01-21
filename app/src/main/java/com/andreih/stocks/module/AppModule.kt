package com.andreih.stocks.module

import com.andreih.stocks.data.repository.StocksRepository
import com.andreih.stocks.data.repository.StocksRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class StocksModule {
    @Binds
    abstract fun bindStocksRepository(
        stocksRepositoryImpl: StocksRepositoryImpl
    ): StocksRepository
}
