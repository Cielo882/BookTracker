package com.cielo.applibros.domain.repository

import androidx.lifecycle.LiveData
import com.cielo.applibros.domain.model.Book
import com.cielo.applibros.domain.model.ReadingStatus

interface BookRepository {
    // Métodos existentes
    fun getAllBooks(): LiveData<List<Book>>
    suspend fun searchBooks(query: String): List<Book>
    suspend fun addToReadList(book: Book)
    suspend fun removeFromReadList(book: Book)
    suspend fun getReadBooks(): List<Book>

    // Nuevos métodos por estado
    fun getBooksByStatus(status: ReadingStatus): LiveData<List<Book>>
    fun getBooksToRead(): LiveData<List<Book>>
    fun getCurrentlyReadingLive(): LiveData<List<Book>>
    fun getFinishedBooks(): LiveData<List<Book>>

    // Métodos para actualizar propiedades
    suspend fun updateReadingStatus(bookId: Int, status: ReadingStatus)
    suspend fun updateRating(bookId: Int, rating: Int?)
    suspend fun updateReview(bookId: Int, review: String?)
    suspend fun updateFavorite(bookId: Int, isFavorite: Boolean)
    suspend fun updateStartDate(bookId: Int, startDate: Long?)
    suspend fun updateFinishDate(bookId: Int, finishDate: Long?)

    // Estadísticas
    suspend fun getTotalBooksRead(): Int
    suspend fun getTotalBooksToRead(): Int
    suspend fun getAverageRating(): Double?
    suspend fun getCurrentlyReading(): List<Book>
    suspend fun getFavoriteBooks(): List<Book>
    fun updateBookRating(bookId: Int, rating: Float)
}