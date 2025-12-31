package com.cielo.applibros

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.cielo.applibros.data.local.preferences.SettingsPreferences
import com.cielo.applibros.domain.model.ThemeMode

class BookTrackerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val prefs = SettingsPreferences(this)
        val theme = prefs.getSettings().themeMode

        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }
}
