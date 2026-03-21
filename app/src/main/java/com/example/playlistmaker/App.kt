package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.domain.api.SettingsInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Главный класс приложения. Служит точкой входа и инициализирует глобальные компоненты.
 */
class App : Application() {
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        // Инициализация Koin: передаем контекст приложения и регистрируем слои архитектуры
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        // Инжектим интерактор настроек, чтобы узнать, какую тему применить при запуске
        val settingsInteractor: SettingsInteractor by inject()
        darkTheme = settingsInteractor.getThemeSettings().isDarkTheme

        // Установка глобального режима темы (Тёмная/Светлая) сразу при старте процесса
        if (darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}