package com.cielo.applibros.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import com.cielo.applibros.domain.model.AppSettings
import com.cielo.applibros.domain.model.Language
import com.cielo.applibros.domain.model.ThemeMode

class SettingsPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "app_settings",
        Context.MODE_PRIVATE
    )

    fun getSettings(): AppSettings {
        val languageCode = prefs.getString(KEY_LANGUAGE, Language.SPANISH.code)
            ?: Language.SPANISH.code
        val themeOrdinal = prefs.getInt(KEY_THEME, ThemeMode.SYSTEM.ordinal)

        return AppSettings(
            language = Language.fromCode(languageCode),
            themeMode = ThemeMode.fromOrdinal(themeOrdinal)
        )
    }

    fun saveLanguage(language: Language) {
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
    }

    fun saveTheme(theme: ThemeMode) {
        prefs.edit().putInt(KEY_THEME, theme.ordinal).apply()
    }

    companion object {
        private const val KEY_LANGUAGE = "language"
        private const val KEY_THEME = "theme"
    }
}