package com.andreih.stocks.module

import android.content.Context
import androidx.room.Room
import com.andreih.stocks.data.StocksDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideStocksDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        StocksDatabase::class.java, "stocks-database"
    ).build()

    @Singleton
    @Provides
    fun provideStocksDao(
        db: StocksDatabase
    ) = db.stockDao()
}