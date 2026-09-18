package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.models.ThemeSettings
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val themeSettingsLiveData = MutableLiveData<ThemeSettings>()
    fun observeThemeSettingsLiveData(): LiveData<ThemeSettings> = themeSettingsLiveData

    init {
        themeSettingsLiveData.value = settingsInteractor.getThemeSettings()
    }

    fun updateThemeSettings(checked: Boolean) {
        val settings = ThemeSettings(checked)
        settingsInteractor.updateThemeSettings(settings)
        themeSettingsLiveData.value = settings
    }

    fun share() {
        sharingInteractor.share()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openAgreement() {
        sharingInteractor.openAgreement()
    }
}