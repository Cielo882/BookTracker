package com.cielo.applibros.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cielo.applibros.data.local.preferences.SettingsPreferences
import com.cielo.applibros.domain.model.AppSettings
import com.cielo.applibros.domain.model.Language
import com.cielo.applibros.domain.model.ThemeMode

class SettingsViewModel(
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    private val _settings = MutableLiveData<AppSettings>()
    val settings: LiveData<AppSettings> = _settings

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _settings.value = settingsPreferences.getSettings()
    }

    fun updateLanguage(language: Language) {
        settingsPreferences.saveLanguage(language)
        loadSettings()
    }

    fun updateTheme(theme: ThemeMode) {
        settingsPreferences.saveTheme(theme)
        loadSettings()
    }

    fun getCurrentLanguage(): Language {
        return _settings.value?.language ?: Language.SPANISH
    }

    fun getCurrentTheme(): ThemeMode {
        return _settings.value?.themeMode ?: ThemeMode.SYSTEM
    }
}