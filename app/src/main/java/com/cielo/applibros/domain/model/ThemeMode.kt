package com.cielo.applibros.domain.model

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM;

    companion object {
        fun fromOrdinal(ordinal: Int): ThemeMode {
            return values().getOrNull(ordinal) ?: SYSTEM
        }
    }
}