package com.andreih.stocks.data.repository

import androidx.datastore.core.DataStore
import com.andreih.stocks.data.model.AppTheme
import com.andreih.stocks.data.model.Settings
import com.andreih.stocks.data.model.into
import com.andreih.stocks.proto.Empty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.andreih.stocks.proto.Settings as SettingsProto
import javax.inject.Inject

interface SettingsRepository {
    val settings: Flow<Settings>

    suspend fun setAppTheme(appTheme: AppTheme)
}

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<SettingsProto>
): SettingsRepository {
    override val settings: Flow<Settings> get() = dataStore.data.map(SettingsProto::into)

    override suspend fun setAppTheme(appTheme: AppTheme) {
        dataStore.updateData {
            val builder = it.toBuilder()
            builder.clearAppTheme()

            when (appTheme) {
                AppTheme.System -> builder.system = Empty.getDefaultInstance()
                AppTheme.Light -> builder.light = Empty.getDefaultInstance()
                AppTheme.Dark -> builder.dark = Empty.getDefaultInstance()
            }

            builder.build()
        }
    }
}