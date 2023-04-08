package com.andreih.stocks.data.model

import com.andreih.stocks.proto.Settings.AppThemeCase

enum class AppTheme {
    System,
    Light,
    Dark
}

data class Settings (
    val appTheme: AppTheme
) {
    companion object {
        val default: Settings = Settings(AppTheme.System)
    }
}

fun AppThemeCase.into(): AppTheme {
    return when (this) {
        AppThemeCase.SYSTEM -> AppTheme.System
        AppThemeCase.LIGHT -> AppTheme.Light
        AppThemeCase.DARK -> AppTheme.Dark
        AppThemeCase.APPTHEME_NOT_SET -> AppTheme.System
    }
}

fun com.andreih.stocks.proto.Settings.into(): Settings {
    return Settings(appTheme = appThemeCase.into())
}