package com.example.playlistmaker.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator

class App : Application() {
    companion object {
        const val SETTINGS_PREFS = "setting_preferences"
    }

    private val settingsInteractor by lazy { Creator.provideSettingsInteractor(this) }

    var isDarkTheme = false

    override fun onCreate() {
        super.onCreate()

        isDarkTheme = settingsInteractor.getThemeSettings().isDarkTheme

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        isDarkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}