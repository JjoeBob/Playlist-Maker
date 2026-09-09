package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.app.App
import com.example.playlistmaker.creator.Creator
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

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App
                val settingsInteractor = Creator.provideSettingsInteractor(app)
                val sharingInteractor = Creator.provideSharingInteractor(app)
                SettingsViewModel(settingsInteractor, sharingInteractor)
            }
        }
    }
}