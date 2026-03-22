package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.search.presentation.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.Executors

val searchModule = module {
    factory<TracksRepository> {
        TracksRepositoryImpl(networkClient = get())
    }

    factory<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(sharedPreferences = get(), gson = get())
    }

    factory<TracksInteractor> {
        TracksInteractorImpl(repository = get(), executor = Executors.newCachedThreadPool())
    }

    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(repository = get())
    }

    viewModel {
        SearchViewModel(
            tracksInteractor = get(),
            searchHistoryInteractor = get(),
            handler = get()
        )
    }
}
