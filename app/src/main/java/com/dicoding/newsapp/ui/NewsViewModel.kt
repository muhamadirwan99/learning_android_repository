package com.dicoding.newsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.newsapp.data.NewsRepository
import com.dicoding.newsapp.data.local.entity.NewsEntity
import kotlinx.coroutines.launch

// ViewModel untuk UI - menghubungkan Repository dengan Activity/Fragment
// Kenapa ViewModel? Untuk memisahkan business logic dari UI dan survive configuration changes
class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    // Expose LiveData dari Repository agar UI bisa observe perubahan data
    // Return langsung dari repository karena tidak ada transform data di layer ini
    fun getHeadlineNews() = newsRepository.getHeadlineNews()

    // Get bookmarked news untuk tab bookmark
    fun getBookmarkedNews() = newsRepository.getBookmarkedNews()

    // Save/bookmark news artikel
    // Kenapa viewModelScope? Agar coroutine auto-cancel ketika ViewModel di-clear (avoid memory leak)
    fun saveNews(news: NewsEntity) {
        viewModelScope.launch {
            // Set bookmark state jadi true di repository
            newsRepository.setBookmarkedNews(news, true)
        }
    }

    // Remove bookmark dari news artikel
    // Kenapa launch coroutine? Karena database operation harus async di background thread
    fun deleteNews(news: NewsEntity) {
        viewModelScope.launch {
            // Set bookmark state jadi false di repository
            newsRepository.setBookmarkedNews(news, false)
        }
    }
}