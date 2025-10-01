package com.cielo.applibros.data.local.entities


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cielo.applibros.domain.model.ReadingStatus

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val authors: List<String>,
    val subjects: List<String>,
    val languages: List<String>,
    val formats: Map<String, String>,

    // Nuevos campos
    val readingStatus: ReadingStatus = ReadingStatus.TO_READ,
    val rating: Int? = null, // 1-5 estrellas
    val startDate: Long? = null, // timestamp
    val finishDate: Long? = null, // timestamp
    val review: String? = null,
    val isFavorite: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)
