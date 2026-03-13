package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingRepository
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.models.ThemeSettings

class SettingsInteractorImpl(private val repository: SettingRepository): SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings = repository.getThemeSettings()
    override fun updateThemeSettings(settings: ThemeSettings) =  repository.updateThemeSettings(settings)
}
