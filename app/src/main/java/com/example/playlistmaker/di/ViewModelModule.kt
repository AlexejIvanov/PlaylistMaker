package com.example.playlistmaker.di

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory { Handler(Looper.getMainLooper()) }

    viewModel {
        PlayerViewModel(
            playerInteractor = get<PlayerInteractor>(),
            handler = get<Handler>()
        )
    }

    viewModel {
        SearchViewModel(
            tracksInteractor = get<TracksInteractor>(),
            searchHistoryInteractor = get<SearchHistoryInteractor>(),
            handler = get<Handler>()
        )
    }

    viewModel {
        SettingsViewModel(
            settingsInteractor = get<SettingsInteractor>()
        )
    }
}
