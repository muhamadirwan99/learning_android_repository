package com.dicoding.newsapp.di

import android.content.Context
import com.dicoding.newsapp.data.NewsRepository
import com.dicoding.newsapp.data.local.room.NewsDatabase
import com.dicoding.newsapp.data.remote.retrofit.ApiConfig
import com.dicoding.newsapp.utils.AppExecutors

// Dependency Injection manual untuk menyediakan Repository ke ViewModel
// Kenapa DI? Agar dependencies tidak hard-coded dan mudah di-test/mock
object Injection {
    fun provideRepository(context: Context): NewsRepository {
        // Dapatkan instance ApiService dari ApiConfig
        val apiService = ApiConfig.getApiService()

        // Dapatkan database instance dan DAO-nya
        val database = NewsDatabase.getInstance(context)
        val dao = database.newsDao()

        // AppExecutors untuk operasi background thread (disk IO, network, main thread)
        val appExecutors = AppExecutors()

        // Return Repository singleton dengan semua dependencies yang dibutuhkan
        // Kenapa pattern ini? Centralized dependency management untuk consistency
        return NewsRepository.getInstance(apiService, dao, appExecutors)
    }
}