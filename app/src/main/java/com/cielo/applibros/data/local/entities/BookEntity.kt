package com.cielo.applibros.data.local.entities


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "read_books")
data class BookEntity(

    @PrimaryKey val id: Int,
    val title: String,
    val authors: String,
    val subjects: String,
    val languages: String,
    val downloadCount: Int,
    val coverUrl: String?,
    val rating: Float = 0f // ← NUEVO: Campo rating

)

