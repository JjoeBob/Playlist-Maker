package com.example.playlistmaker.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    companion object {
        const val SETTINGS_PREFS = "setting_preferences"
        const val DARK_THEME_KEY = "dark_theme_enabled"
    }

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(SETTINGS_PREFS, MODE_PRIVATE)

        darkTheme = sharedPrefs.getBoolean(DARK_THEME_KEY, false)

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