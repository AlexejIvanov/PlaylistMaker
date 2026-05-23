package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.presentation.playlists.PlaylistsViewModule
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin-модуль для предоставления зависимостей раздела "Медиатека".
 */
val mediaModule = module {

    // ViewModel для управления логикой вкладки "Плейлисты"
    viewModel {
        PlaylistsViewModule()
    }

}
