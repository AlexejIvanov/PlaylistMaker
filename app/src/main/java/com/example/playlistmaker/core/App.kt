package com.example.playlistmaker.core

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.core.di.coreModule
import com.example.playlistmaker.core.di.dataModule
import com.example.playlistmaker.favorite.di.favoriteModule
import com.example.playlistmaker.media.di.mediaModule
import com.example.playlistmaker.player.di.playerModule
import com.example.playlistmaker.playlist.di.playlistModule
import com.example.playlistmaker.search.di.searchModule
import com.example.playlistmaker.settings.di.settingsModule
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
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
            modules(
                coreModule,
                searchModule,
                playerModule,
                settingsModule,
                mediaModule,
                dataModule,
                favoriteModule,
                playlistModule
            )
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
