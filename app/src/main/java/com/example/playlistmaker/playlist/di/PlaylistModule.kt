package com.example.playlistmaker.playlist.di

import com.example.playlistmaker.playlist.data.repository.PlaylistRepositoryImpl
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.api.PlaylistRepository
import com.example.playlistmaker.playlist.domain.impl.PlaylistInteractorImpl
import com.example.playlistmaker.playlist.presentation.create.CreatePlaylistViewModel
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistModule = module {
    factory { Gson() }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), androidContext())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }

    viewModel {
        CreatePlaylistViewModel(get())
    }
}