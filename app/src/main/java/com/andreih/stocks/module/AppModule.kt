package com.andreih.stocks.module

import com.andreih.stocks.data.repository.StocksRepository
import com.andreih.stocks.data.repository.StocksRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StocksModule {

    @Singleton
    @Binds
    abstract fun bindStocksRepository(
        stocksRepositoryImpl: StocksRepositoryImpl
    ): StocksRepository
}
