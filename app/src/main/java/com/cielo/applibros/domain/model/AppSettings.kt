package com.cielo.applibros.domain.model

data class AppSettings(
    val language: Language = Language.SPANISH,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)