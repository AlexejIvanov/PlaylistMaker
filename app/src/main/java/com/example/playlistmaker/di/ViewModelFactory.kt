package com.example.playlistmaker.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.presentation.settings.SettingsViewModel

class ViewModelFactory(context: Context) : ViewModelProvider.Factory {
    // Сохраняем applicationContext, чтобы избежать утечек памяти
    private val appContext = context.applicationContext

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PlayerViewModel::class.java) -> {
                PlayerViewModel(Creator.providePlayerInteractor()) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(
                    Creator.provideTracksInteractor(appContext),
                    Creator.provideSearchHistoryInteractor(appContext)
                ) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(Creator.provideSettingsInteractor(appContext)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}