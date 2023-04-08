package com.andreih.stocks.module

import android.content.Context
import androidx.datastore.core.DataStore
import com.andreih.stocks.data.serializer.settingsDataStore
import com.andreih.stocks.proto.Settings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
object SettingsDataStoreModule {

    @Provides
    fun providesSettingsDataStore(
        @ActivityContext context: Context,
    ): DataStore<Settings> = context.settingsDataStore
}
