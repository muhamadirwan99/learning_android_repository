package com.dicoding.newsapp.data.remote.retrofit

import com.dicoding.newsapp.data.remote.response.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Interface yang mendefinisikan endpoint API
// Kenapa interface? Retrofit akan auto-generate implementasinya
interface ApiService {
    // GET request ke endpoint top-headlines dengan filter country=us dan category=science
    // @Query untuk menambahkan apiKey sebagai query parameter di URL
    // Kenapa suspend? Agar bisa dipanggil di coroutine (async operation tanpa blocking main thread)
    @GET("top-headlines?country=us&category=science")
    suspend fun getNews(@Query("apiKey") apiKey: String): NewsResponse
}