package com.example.playlistmaker.core.di

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.core.network.NetworkClient
import com.example.playlistmaker.core.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.ITunesApiService
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val coreModule = module {
    single { Gson() }

    single<ITunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    single<SharedPreferences> {
        androidContext().getSharedPreferences("playlist_maker_preferences", Context.MODE_PRIVATE)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(context = androidContext(), iTunesService = get())
    }

    factory { Handler(Looper.getMainLooper()) }
}
