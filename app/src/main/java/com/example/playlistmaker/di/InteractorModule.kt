package com.example.playlistmaker.di

import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.SettingRepository
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import org.koin.dsl.module
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val interactorModule = module {
    single<ExecutorService> { Executors.newCachedThreadPool() }

    // Явно указываем типы
    factory<PlayerInteractor> { PlayerInteractorImpl(repository = get<PlayerRepository>()) }
    single<SearchHistoryInteractor> { SearchHistoryInteractorImpl(repository = get<SearchHistoryRepository>()) }
    single<SettingsInteractor> { SettingsInteractorImpl(repository = get<SettingRepository>()) }
    single<TracksInteractor> { TracksInteractorImpl(repository = get<TracksRepository>(), executor = get<ExecutorService>()) }
}
