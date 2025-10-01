package com.cielo.applibros.domain.usecase

import com.cielo.applibros.domain.model.ReadingStatus
import com.cielo.applibros.domain.repository.BookRepository



class UpdateBookStatusUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookId: Int, status: ReadingStatus) {
        repository.updateReadingStatus(bookId, status)

        // Actualizar fechas automáticamente
        when (status) {
            ReadingStatus.READING -> {
                repository.updateStartDate(bookId, System.currentTimeMillis())
            }
            ReadingStatus.FINISHED -> {
                repository.updateFinishDate(bookId, System.currentTimeMillis())
            }
            else -> { /* No action needed */ }
        }
    }
}