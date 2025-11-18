package com.cielo.applibros.domain.usecase


import com.cielo.applibros.domain.repository.BookRepository

class UpdateFinishDateUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookId: Int, finishDate: Long) {
        repository.updateFinishDate(bookId, finishDate)
    }
}