package com.cielo.applibros.domain.usecase

import androidx.lifecycle.LiveData
import com.cielo.applibros.domain.model.Book
import com.cielo.applibros.domain.repository.BookRepository

class GetCurrentlyReadingUseCase(
    private val repository: BookRepository
) {
    // Método para obtener LiveData directamente (recomendado)
    fun observeCurrentlyReading(): LiveData<List<Book>> {
        return repository.getCurrentlyReadingLive()
    }

    // Método suspend para obtener snapshot actual
    suspend operator fun invoke(): List<Book> {
        return repository.getCurrentlyReadingLive().value ?: emptyList()
    }
}