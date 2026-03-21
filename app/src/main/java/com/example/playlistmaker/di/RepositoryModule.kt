package com.example.playlistmaker.di

import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.SettingRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.google.gson.Gson
import org.koin.dsl.module

/**
 * Koin-модуль для предоставления реализаций репозиториев (Data Layer).
 */
val repositoryModule = module {

    // Создает новый экземпляр системного медиаплеера при каждом запросе
    factory { MediaPlayer() }

    // Репозиторий для управления состоянием и действиями плеера
    factory<PlayerRepository> { PlayerRepositoryImpl(mediaPlayer = get<MediaPlayer>()) }

    // Репозиторий для сохранения и получения истории поиска из SharedPreferences
    factory<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(sharedPreferences = get<SharedPreferences>(), gson = get<Gson>())
    }

    // Репозиторий для управления настройками темы оформления
    factory<SettingRepository> { SettingsRepositoryImpl(sharedPreferences = get<SharedPreferences>()) }

    // Репозиторий для выполнения поиска треков через сетевой клиент
    factory<TracksRepository> { TracksRepositoryImpl(networkClient = get<NetworkClient>()) }
}