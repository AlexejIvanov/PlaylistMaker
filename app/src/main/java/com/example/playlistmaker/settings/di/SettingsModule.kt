package com.example.playlistmaker.settings.di

import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingRepository
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    factory<SettingRepository> {
        SettingsRepositoryImpl(sharedPreferences = get())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(repository = get())
    }

    viewModel {
        SettingsViewModel(
            settingsInteractor = get()
        )
    }
}
