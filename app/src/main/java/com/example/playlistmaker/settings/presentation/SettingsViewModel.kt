package com.example.playlistmaker.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.core.models.ThemeSettings

/**
 * ViewModel для экрана настроек.
 * Управляет состоянием темы оформления и взаимодействует с бизнес-логикой (Interactor).
 */
class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    // LiveData для передачи состояния темы в Activity (true — тёмная, false — светлая)
    private val _themeState = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> get() = _themeState

    init {
        // При инициализации загружаем сохранённое значение темы из настроек
        _themeState.value = settingsInteractor.getThemeSettings().isDarkTheme
    }

    /**
     * Переключает тему приложения: сохраняет в настройки и обновляет LiveData для UI.
     */
    fun switchTheme(isDarkTheme: Boolean) {
        settingsInteractor.updateThemeSettings(ThemeSettings(isDarkTheme))
        _themeState.value = isDarkTheme
    }
}
