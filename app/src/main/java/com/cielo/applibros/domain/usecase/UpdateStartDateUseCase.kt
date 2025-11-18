package com.cielo.applibros.domain.usecase

import com.cielo.applibros.domain.repository.BookRepository

class UpdateStartDateUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookId: Int, startDate: Long) {
        repository.updateStartDate(bookId, startDate)
    }
}
