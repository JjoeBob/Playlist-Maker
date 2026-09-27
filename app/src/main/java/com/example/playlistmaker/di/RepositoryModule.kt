package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.AudioPlayerImpl
import com.example.playlistmaker.player.domain.api.AudioPlayer
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.ResourceProviderImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.ResourceProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    factory<AudioPlayer> {
        AudioPlayerImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    single<ResourceProvider> {
        ResourceProviderImpl(androidContext())
    }
}