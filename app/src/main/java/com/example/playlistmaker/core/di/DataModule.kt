package com.example.playlistmaker.core.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.playlistmaker.search.data.network.ITunesApiService
import com.example.playlistmaker.core.network.NetworkClient
import com.example.playlistmaker.core.network.RetrofitNetworkClient
import com.example.playlistmaker.favorite.data.db.AppDatabase
import com.example.playlistmaker.favorite.data.db.TrackDbConvertor
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Koin-модуль для предоставления зависимостей уровня данных (Data Layer).
 */
val dataModule = module {

    // Единственный экземпляр Gson для работы с JSON
    single { Gson() }

    // Настройка и создание сервиса iTunes API через Retrofit
    single<ITunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    // Хранилище SharedPreferences для сохранения настроек и истории
    single<SharedPreferences> {
        androidContext().getSharedPreferences("playlist_maker_preferences", Context.MODE_PRIVATE)
    }

    // Реализация сетевого клиента, использующая Retrofit и контекст для проверки связи
    single<NetworkClient> {
        RetrofitNetworkClient(context = androidContext(), iTunesService = get())
    }

    //Инициализация базы данных
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    single { get<AppDatabase>().favoriteTracksDao() }

    factory { TrackDbConvertor() }

}

