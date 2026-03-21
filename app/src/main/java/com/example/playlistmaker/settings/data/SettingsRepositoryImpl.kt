package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.playlistmaker.core.models.ThemeSettings
import com.example.playlistmaker.settings.domain.api.SettingRepository

/**
 * Репозиторий для управления настройками приложения (в данном случае темой оформления).
 */
class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingRepository {

    companion object {
        const val SWITCH_THEME_KEY = "switch_theme_key" // Ключ для хранения флага темы
    }

    // Читает состояние темной темы из настроек (по умолчанию — false)
    override fun getThemeSettings(): ThemeSettings {
        val isDark = sharedPreferences.getBoolean(SWITCH_THEME_KEY, false)
        return ThemeSettings(isDark)
    }

    // Сохраняет выбор темы и сразу применяет её глобано для всего приложения
    override fun updateThemeSettings(settings: ThemeSettings) {
        sharedPreferences.edit { putBoolean(SWITCH_THEME_KEY, settings.isDarkTheme) }

        // Переключает режим "День/Ночь" в системных настройках приложения
        AppCompatDelegate.setDefaultNightMode(
            if (settings.isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}