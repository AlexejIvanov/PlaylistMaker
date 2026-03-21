package com.example.playlistmaker.domain.models

/**
 * Доменная модель настроек темы оформления приложения.
 */
data class ThemeSettings (
    val isDarkTheme: Boolean // Флаг состояния: true — включена тёмная тема, false — светлая
)