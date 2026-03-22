package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.core.models.ThemeSettings

/**
 * Интерфейс репозитория для работы с настройками приложения.
 * Определяет контракт для сохранения и получения темы оформления в слое Data.
 */
interface SettingRepository {
    fun getThemeSettings(): ThemeSettings           // Получить текущие настройки темы (темная/светлая)
    fun updateThemeSettings(settings: ThemeSettings) // Сохранить и применить новые настройки темы
}