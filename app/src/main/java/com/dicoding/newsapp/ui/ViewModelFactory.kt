package com.dicoding.newsapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.newsapp.data.NewsRepository
import com.dicoding.newsapp.di.Injection

// Custom ViewModelFactory untuk inject Repository ke ViewModel
// Kenapa perlu Factory? Karena ViewModel butuh parameter (repository), tidak bisa pakai default constructor
class ViewModelFactory private constructor(private val newsRepository: NewsRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Cek apakah ViewModel yang diminta adalah NewsViewModel
        // Kenapa perlu cek? Untuk memastikan kita create ViewModel yang benar
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(newsRepository) as T
        }
        // Throw exception jika ViewModel class tidak dikenali
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        // Singleton pattern untuk Factory
        // Kenapa singleton? Agar Repository instance yang sama dipakai di semua ViewModel
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                // Inject Repository dari Injection class (Dependency Injection)
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { it }
    }
}