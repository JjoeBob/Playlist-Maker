package com.example.playlistmaker.di

import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel<PlayerViewModel> { (track: Track) ->
        PlayerViewModel(track, get(), get(), get())
    }

    viewModel<SearchViewModel> {
        SearchViewModel(get(), get(), get())
    }

    viewModel<SettingsViewModel> {
        SettingsViewModel(get(), get())
    }
}