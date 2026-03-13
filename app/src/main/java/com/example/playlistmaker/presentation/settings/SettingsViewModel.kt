package com.example.playlistmaker.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.models.ThemeSettings

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val _themeState = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> get() = _themeState

    init {
        // Устанавливаем текущую тему при инициализации
        _themeState.value = settingsInteractor.getThemeSettings().isDarkTheme
    }

    fun switchTheme(isDarkTheme: Boolean) {
        settingsInteractor.updateThemeSettings(ThemeSettings(isDarkTheme))
        _themeState.value = isDarkTheme
    }
}