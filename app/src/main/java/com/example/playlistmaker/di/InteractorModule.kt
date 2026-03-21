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

/**
 * Koin-модуль для предоставления интеракторов (бизнес-логики) приложения.
 */
val interactorModule = module {

    // Пул потоков для выполнения фоновых задач (например, сетевых запросов)
    single<ExecutorService> { Executors.newCachedThreadPool() }

    // Интерактор для управления плеером (создается новый экземпляр при каждом запросе)
    factory<PlayerInteractor> { PlayerInteractorImpl(repository = get<PlayerRepository>()) }

    // Интерактор для работы с историей поиска треков
    factory<SearchHistoryInteractor> { SearchHistoryInteractorImpl(repository = get<SearchHistoryRepository>()) }

    // Интерактор для управления настройками (например, темой оформления)
    factory<SettingsInteractor> { SettingsInteractorImpl(repository = get<SettingRepository>()) }

    // Интерактор для поиска треков, использующий ExecutorService для асинхронности
    factory<TracksInteractor> {
        TracksInteractorImpl(repository = get<TracksRepository>(), executor = get<ExecutorService>())
    }
}