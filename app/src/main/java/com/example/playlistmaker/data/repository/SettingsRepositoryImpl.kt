package com.example.playlistmaker.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.SettingRepository
import com.example.playlistmaker.domain.models.ThemeSettings
import androidx.core.content.edit

class SettingsRepositoryImpl(context: Context) : SettingRepository {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val SWITCH_THEME_KEY = "switch_theme_key"
    }

    override fun getThemeSettings(): ThemeSettings {
        val isDark = sharedPreferences.getBoolean(SWITCH_THEME_KEY, false)
        return ThemeSettings(isDark)
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        sharedPreferences.edit { putBoolean(SWITCH_THEME_KEY, settings.isDarkTheme) }
        AppCompatDelegate.setDefaultNightMode(
            if (settings.isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
