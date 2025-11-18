package com.cielo.applibros.domain.usecase

import com.cielo.applibros.domain.model.ReadingStatus
import com.cielo.applibros.domain.repository.BookRepository



class UpdateBookStatusUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookId: Int, status: ReadingStatus) {
        // Solo actualizar el estado, SIN tocar las fechas
        repository.updateReadingStatus(bookId, status)
    }
}