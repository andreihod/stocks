package com.andreih.stocks.module

import com.andreih.stocks.data.repository.StocksRepository
import com.andreih.stocks.data.repository.StocksRepositoryImpl
import com.andreih.stocks.network.AlphaVantageNetworkDataSource
import com.andreih.stocks.network.retrofit.RetrofitAlphaVantageNetwork
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
    abstract fun bindAlphaVantageNetworkDataSource(
        alphaVantageNetworkDataSource: RetrofitAlphaVantageNetwork
    ): AlphaVantageNetworkDataSource
}