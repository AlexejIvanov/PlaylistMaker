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

val repositoryModule = module {
    factory { MediaPlayer() }


    factory<PlayerRepository> { PlayerRepositoryImpl(mediaPlayer = get<MediaPlayer>()) }
    factory<SearchHistoryRepository> { SearchHistoryRepositoryImpl(sharedPreferences = get<SharedPreferences>(), gson = get<Gson>()) }
    factory<SettingRepository> { SettingsRepositoryImpl(sharedPreferences = get<SharedPreferences>()) }
    factory<TracksRepository> { TracksRepositoryImpl(networkClient = get<NetworkClient>()) }
}
