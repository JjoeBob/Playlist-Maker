package com.example.playlistmaker.settings.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.models.ThemeSettings
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val themeSettingsLiveData = MutableLiveData<ThemeSettings>()
    fun getThemeSettingsLiveData(): LiveData<ThemeSettings> = themeSettingsLiveData
    
    fun isDarkTheme() = settingsInteractor.getThemeSettings().isDarkTheme

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
        fun getFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appContext = context.applicationContext
                val settingsInteractor = Creator.provideSettingsInteractor(appContext)
                val sharingInteractor = Creator.provideSharingInteractor(appContext)
                SettingsViewModel(settingsInteractor, sharingInteractor)
            }
        }
    }
}