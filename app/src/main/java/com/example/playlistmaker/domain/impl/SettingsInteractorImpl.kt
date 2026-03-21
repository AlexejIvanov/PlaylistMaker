package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingRepository
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.models.ThemeSettings

/**
 * Реализация интерактора для управления настройками приложения.
 */
class SettingsInteractorImpl(private val repository: SettingRepository): SettingsInteractor {

    // Получение текущего состояния темы (светлая/темная)
    override fun getThemeSettings(): ThemeSettings = repository.getThemeSettings()

    // Обновление и сохранение новых настроек темы
    override fun updateThemeSettings(settings: ThemeSettings) =  repository.updateThemeSettings(settings)
}