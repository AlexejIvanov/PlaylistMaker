package com.example.playlistmaker.player.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.player.presentation.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    factory { MediaPlayer() }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(mediaPlayer = get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(repository = get())
    }

    viewModel {
        PlayerViewModel(get(), get(), get())
    }
}
