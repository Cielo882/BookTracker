package com.cielo.applibros.data.repository

import com.cielo.applibros.data.local.dao.BookDao
import com.cielo.applibros.data.local.entities.BookEntity
import com.cielo.applibros.data.remote.api.GutendexApiService
import com.cielo.applibros.data.remote.dto.BookDto
import com.cielo.applibros.domain.model.Book
import com.cielo.applibros.domain.repository.BookRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BookRepositoryImpl(
    private val apiService: GutendexApiService,
    private val bookDao: BookDao
) : BookRepository {

    private val gson = Gson()



    override suspend fun searchBooks(query: String): List<Book> {
        val response = apiService.searchBooks(query)
        val readBooks = bookDao.getAllReadBooks()
        val readMap = readBooks.associateBy { it.id }

        return response.results.map { dto ->
            val leido = readMap[dto.id]
            dto.toDomain().copy(
                isRead = leido != null,
                rating = leido?.rating ?: 0f // Mantener rating si existe
            )
        }
    }


    override suspend fun getReadBooks(): List<Book> {
        val entities = bookDao.getAllReadBooks()
        return entities.map { it.toDomain() }
    }

    override suspend fun addToRead(book: Book) {
        bookDao.insertBook(book.toEntity())
    }

    override suspend fun removeFromRead(bookId: Int) {
        bookDao.deleteBook(bookId)
    }

    override suspend fun getBookById(id: Int): Book? {
        val entity = bookDao.getBookById(id)
        return entity?.toDomain()
    }

    override suspend fun updateBookRating(bookId: Int, rating: Float) {
        bookDao.updateBookRating(bookId, rating)
    }

    // Mappers actualizados
    private fun BookDto.toDomain(): Book {
        val coverUrl = formats["image/jpeg"] ?: formats["image/png"]
        return Book(
            id = id,
            title = title,
            authors = authors.map { it.name },
            subjects = subjects,
            languages = languages,
            downloadCount = downloadCount,
            coverUrl = coverUrl,
            rating = 0f
        )
    }

    private fun Book.toEntity(): BookEntity {
        return BookEntity(
            id = id,
            title = title,
            authors = gson.toJson(authors),
            subjects = gson.toJson(subjects),
            languages = gson.toJson(languages),
            downloadCount = downloadCount,
            coverUrl = coverUrl,
            rating = rating
        )
    }

    private fun BookEntity.toDomain(): Book {
        val authorsType = object : TypeToken<List<String>>() {}.type
        val subjectsType = object : TypeToken<List<String>>() {}.type
        val languagesType = object : TypeToken<List<String>>() {}.type

        return Book(
            id = id,
            title = title,
            authors = gson.fromJson(authors, authorsType),
            subjects = gson.fromJson(subjects, subjectsType),
            languages = gson.fromJson(languages, languagesType),
            downloadCount = downloadCount,
            coverUrl = coverUrl,
            isRead = true,
            rating = rating
        )
    }
}