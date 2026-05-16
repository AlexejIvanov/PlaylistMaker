package com.example.playlistmaker.core

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.core.di.coreModule
import com.example.playlistmaker.media.di.mediaModule
import com.example.playlistmaker.player.di.playerModule
import com.example.playlistmaker.search.di.searchModule
import com.example.playlistmaker.settings.di.settingsModule
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.google.android.material.bottomnavigation.BottomNavigationView
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
            modules(coreModule, searchModule, playerModule, settingsModule, mediaModule)
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

/**
 * Главный экран приложения.
 * Обеспечивает навигацию между основными разделами: Поиск, Медиатека и Настройки.
 */
class RootActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        // Скрываем BottomNav на экране плеера
        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavigationView.isVisible = when (destination.id) {
                R.id.playerFragment -> false
                else -> true
            }
        }
    }
}