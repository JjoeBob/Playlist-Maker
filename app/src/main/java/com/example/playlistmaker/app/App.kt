package com.example.playlistmaker.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Creator

class App : Application() {
    companion object {
        const val SETTINGS_PREFS = "setting_preferences"
    }

    private val settingsInteractor by lazy { Creator.provideSettingsInteractor(this) }

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        darkTheme = settingsInteractor.getThemeSettings().isDarkTheme

        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}