package com.dicoding.newsapp.data.remote.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton object untuk konfigurasi Retrofit client
// Kenapa object? Agar hanya ada 1 instance ApiService di seluruh aplikasi (efisiensi)
object ApiConfig {
    fun getApiService(): ApiService {
        // Logging interceptor untuk melihat request/response di Logcat (debug purpose)
        // Level.BODY agar bisa lihat detail isi request dan response
        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        // OkHttpClient dengan interceptor untuk logging network calls
        // Kenapa perlu? Untuk debugging dan monitoring API calls
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        // Build Retrofit instance dengan base URL News API
        // GsonConverterFactory untuk auto-convert JSON response ke data class
        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // Create implementation ApiService interface menggunakan Retrofit
        return retrofit.create(ApiService::class.java)
    }
}