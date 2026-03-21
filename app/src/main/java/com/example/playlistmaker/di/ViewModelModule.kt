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

/**
 * Koin-модуль для предоставления ViewModels (Presentation Layer).
 */
val viewModelModule = module {

    // Создает Handler, привязанный к главному (UI) потоку для отложенных задач
    factory { Handler(Looper.getMainLooper()) }

    // ViewModel для экрана аудиоплеера: управляет состоянием проигрывания и таймером
    viewModel {
        PlayerViewModel(
            playerInteractor = get<PlayerInteractor>(),
            handler = get<Handler>()
        )
    }

    // ViewModel для экрана поиска: отвечает за поисковые запросы, историю и Debounce
    viewModel {
        SearchViewModel(
            tracksInteractor = get<TracksInteractor>(),
            searchHistoryInteractor = get<SearchHistoryInteractor>(),
            handler = get<Handler>()
        )
    }

    // ViewModel для экрана настроек: управление темой и логикой кнопок "Поделиться"
    viewModel {
        SettingsViewModel(
            settingsInteractor = get<SettingsInteractor>()
        )
    }
}