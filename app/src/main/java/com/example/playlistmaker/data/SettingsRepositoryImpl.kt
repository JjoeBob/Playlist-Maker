package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.models.ThemeSettings
import androidx.core.content.edit

class SettingsRepositoryImpl(private val sharedPrefs: SharedPreferences) : SettingsRepository {
    companion object {
        private const val DARK_THEME_KEY = "key_for_dark_theme"
    }

    override fun getThemeSettings(): ThemeSettings {
        val isDark = sharedPrefs.getBoolean(DARK_THEME_KEY, false)
        return ThemeSettings(isDark)
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        sharedPrefs.edit {
            putBoolean(DARK_THEME_KEY, settings.isDarkTheme)
        }
    }
}