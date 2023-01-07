package com.andreih.stocks.module

import com.andreih.stocks.data.repository.StocksRepository
import com.andreih.stocks.data.repository.StocksRepositoryImpl
import com.andreih.stocks.network.MarketStackNetworkDataSource
import com.andreih.stocks.network.retrofit.RetrofitMarketStackNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class StocksModule {
    @Binds
    abstract fun bindStocksRepository(
        stocksRepositoryImpl: StocksRepositoryImpl
    ): StocksRepository

    @Binds
    abstract fun bindMarketStackNetworkDataSource(
        marketStackNetworkDataSource: RetrofitMarketStackNetwork
    ): MarketStackNetworkDataSource
}