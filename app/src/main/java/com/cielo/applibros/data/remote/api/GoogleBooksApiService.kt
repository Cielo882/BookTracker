package com.cielo.applibros.data.remote.api


import com.cielo.applibros.data.remote.dto.GoogleBooksSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("books/v1/volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20
    ): GoogleBooksSearchResponse

    companion object {
        const val BASE_URL = "https://www.googleapis.com/"
    }
}
