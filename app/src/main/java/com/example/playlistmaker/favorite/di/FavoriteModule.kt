package com.example.playlistmaker.favorite.di

import com.example.playlistmaker.favorite.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.favorite.domain.db.FavoriteTrackRepository
import com.example.playlistmaker.favorite.domain.db.FavoriteTracksInteractorImpl
import com.example.playlistmaker.favorite.domain.db.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.media.presentation.favorites.FavoriteTracksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val favoriteModule = module {

    // Репозиторий для работы с избранным
    single<FavoriteTrackRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    // Интерактор для работы с избранным
    single<FavoriteTrackInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

    // ViewModel для экрана "Медиатека -> Избранное"
    viewModel {
        FavoriteTracksViewModel(get())
    }
}
