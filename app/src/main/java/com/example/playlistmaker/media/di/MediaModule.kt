package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.presentation.favorites.FavoriteTracksViewModule
import com.example.playlistmaker.media.presentation.playlists.PlaylistsViewModule
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {

    viewModel {
        FavoriteTracksViewModule()
    }

    viewModel {
        PlaylistsViewModule()
    }

}