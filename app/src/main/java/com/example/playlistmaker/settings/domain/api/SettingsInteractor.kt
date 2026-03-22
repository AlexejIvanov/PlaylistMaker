package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.core.models.ThemeSettings

/**
 * Интерфейс бизнес-логики для управления настройками приложения.
 */
interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings           // Получить текущие настройки темы оформления
    fun updateThemeSettings(settings: ThemeSettings) // Обновить и применить настройки темы
}