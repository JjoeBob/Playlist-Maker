package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.app.App
import com.example.playlistmaker.data.AudioPlayerImpl
import com.example.playlistmaker.data.ExternalNavigatorImpl
import com.example.playlistmaker.data.ResourceProviderImpl
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl.Companion.SEARCH_HISTORY_PREFS
import com.example.playlistmaker.data.SettingsRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.ItunesNetworkClient
import com.example.playlistmaker.domain.api.AudioPlayer
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.api.ExternalNavigator
import com.example.playlistmaker.domain.api.ResourceProvider
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.api.SharingInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.impl.SharingInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.google.gson.Gson

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(ItunesNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        val sharedPrefs = context.getSharedPreferences(App.SETTINGS_PREFS, Context.MODE_PRIVATE)
        return SettingsRepositoryImpl(sharedPrefs)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    private fun getResourceProvider(context: Context): ResourceProvider {
        return ResourceProviderImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(context), getResourceProvider(context))
    }

    private fun getAudioPlayer(): AudioPlayer {
        return AudioPlayerImpl()
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getAudioPlayer())
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val sharedPrefs = context.getSharedPreferences(
            SEARCH_HISTORY_PREFS,
            Context.MODE_PRIVATE
        )
        val gson = Gson()

        return SearchHistoryRepositoryImpl(sharedPrefs, gson)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }
}