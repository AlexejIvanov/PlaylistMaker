package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.core.models.ThemeSettings
import com.example.playlistmaker.settings.domain.api.SettingRepository
import com.example.playlistmaker.settings.domain.api.SettingsInteractor

/**
 * Реализация интерактора для управления настройками приложения.
 */
class SettingsInteractorImpl(private val repository: SettingRepository): SettingsInteractor {

    // Получение текущего состояния темы (светлая/темная)
    override fun getThemeSettings(): ThemeSettings = repository.getThemeSettings()

    // Обновление и сохранение новых настроек темы
    override fun updateThemeSettings(settings: ThemeSettings) =  repository.updateThemeSettings(settings)
}