package com.cielo.applibros.data.remote

import com.cielo.applibros.data.remote.api.GutendexApiService
import com.cielo.applibros.data.remote.api.OpenLibraryApiService
import com.cielo.applibros.data.remote.api.GoogleBooksApiService
import com.cielo.applibros.domain.model.Book
import com.cielo.applibros.domain.model.Language
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class UnifiedBookSearchService(
    private val gutendexApi: GutendexApiService,
    private val openLibraryApi: OpenLibraryApiService,
    private val googleBooksApi: GoogleBooksApiService
) {

    // AGREGAR parámetro de idioma
    suspend fun searchBooks(query: String, language: Language = Language.SPANISH): List<Book> = coroutineScope {
        val results = mutableListOf<Book>()

        // Buscar en paralelo
        val gutendexDeferred = async { searchGutendex(query) }
        val openLibraryDeferred = async { searchOpenLibrary(query) }
        val googleBooksDeferred = async { searchGoogleBooks(query) }

        try {
            results.addAll(gutendexDeferred.await())
        } catch (e: Exception) { }

        try {
            results.addAll(openLibraryDeferred.await())
        } catch (e: Exception) { }

        try {
            results.addAll(googleBooksDeferred.await())
        } catch (e: Exception) { }

        // FILTRAR por idioma
        val filteredResults = results.filter { book ->
            book.languages.any { it.contains(language.code, ignoreCase = true) }
        }

        // Si no hay resultados en el idioma específico, devolver todos
        val finalResults = filteredResults.ifEmpty { results }

        // Eliminar duplicados
        finalResults.distinctBy { "${it.title.lowercase()}_${it.authorsString.lowercase()}" }
    }

    private suspend fun searchGutendex(query: String): List<Book> {
        return try {
            val response = gutendexApi.searchBooks(query)
            response.results.map { dto ->
                Book(
                    id = dto.id,
                    title = dto.title,
                    authors = dto.authors.map { it.name },
                    subjects = dto.subjects,
                    languages = dto.languages,
                    formats = dto.formats
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun searchOpenLibrary(query: String): List<Book> {
        return try {
            val response = openLibraryApi.searchBooks(query)
            response.docs.mapIndexed { index, dto ->
                // Generar ID único basado en OpenLibrary
                val bookId = dto.key.hashCode() + 1000000 // Offset para evitar conflictos

                Book(
                    id = bookId,
                    title = dto.title ?: "Unknown",
                    authors = dto.authorName ?: listOf("Unknown"),
                    subjects = dto.subject?.take(5) ?: emptyList(),
                    languages = dto.language ?: emptyList(),
                    formats = buildMap {
                        dto.toCoverUrl()?.let { put("image/jpeg", it) }
                    }
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun searchGoogleBooks(query: String): List<Book> {
        return try {
            val response = googleBooksApi.searchBooks(query)
            response.items?.mapIndexed { index, item ->
                // Generar ID único basado en Google Books
                val bookId = item.id.hashCode() + 2000000 // Offset diferente

                Book(
                    id = bookId,
                    title = item.volumeInfo.title ?: "Unknown",
                    authors = item.volumeInfo.authors ?: listOf("Unknown"),
                    subjects = item.volumeInfo.categories ?: emptyList(),
                    languages = item.volumeInfo.language?.let { listOf(it) } ?: emptyList(),
                    formats = buildMap {
                        item.volumeInfo.imageLinks?.thumbnail?.let {
                            // Convertir a HTTPS y quitar zoom
                            val httpsUrl = it.replace("http://", "https://")
                            put("image/jpeg", httpsUrl)
                        }
                    }
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}