package com.cielo.applibros.data.remote.api


import com.cielo.applibros.data.remote.dto.BookSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GutendexApiService {
    @GET("books")
    suspend fun searchBooks(
        @Query("search") query: String,
        @Query("page") page: Int = 1
    ): BookSearchResponse

    companion object {
        const val BASE_URL = "https://gutendex.com/"
    }
}