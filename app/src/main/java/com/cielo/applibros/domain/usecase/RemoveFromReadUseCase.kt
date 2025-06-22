package com.cielo.applibros.domain.usecase

import com.cielo.applibros.domain.repository.BookRepository

class RemoveFromReadUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(bookId: Int) {
        repository.removeFromRead(bookId)
    }
}