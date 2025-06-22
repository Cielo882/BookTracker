package com.cielo.applibros.domain.model

data class Book(
    val id: Int,
    val title: String,
    val authors: List<String>,
    val subjects: List<String>,
    val languages: List<String>,
    val downloadCount: Int,
    val coverUrl: String?,
    val isRead: Boolean = false,
    val rating: Float = 0f //  Calificación 0-5 estrellas
)