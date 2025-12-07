package com.cielo.applibros.data.remote.api


import com.cielo.applibros.data.remote.dto.OpenLibrarySearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApiService {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20
    ): OpenLibrarySearchResponse

    companion object {
        const val BASE_URL = "https://openlibrary.org/"
    }
}