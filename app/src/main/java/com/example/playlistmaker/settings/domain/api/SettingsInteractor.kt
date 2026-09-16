package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.settings.domain.models.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings() : ThemeSettings

    fun updateThemeSettings(themeSettings: ThemeSettings)
}