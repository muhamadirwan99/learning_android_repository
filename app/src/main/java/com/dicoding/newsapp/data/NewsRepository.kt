package com.dicoding.newsapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.dicoding.newsapp.BuildConfig
import com.dicoding.newsapp.data.local.entity.NewsEntity
import com.dicoding.newsapp.data.local.room.NewsDao
import com.dicoding.newsapp.data.remote.response.NewsResponse
import com.dicoding.newsapp.data.remote.retrofit.ApiService
import com.dicoding.newsapp.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Repository sebagai single source of truth - mengelola data dari API dan Database
// Kenapa perlu Repository? Untuk memisahkan logika data dari UI (separation of concerns)
class NewsRepository private constructor(
    private val apiService: ApiService, private val newsDao: NewsDao, private val appExecutors: AppExecutors
) {

    // Mengambil headline news dengan strategi: Network First, kemudian Database
    // Kenapa pattern ini? Agar data selalu fresh dari API, tapi tetap bisa offline dari cache
    fun getHeadlineNews(): LiveData<Result<List<NewsEntity>>> = liveData {
        // Emit Loading state dulu agar UI bisa tampilkan progress indicator
        emit(Result.Loading)
        try {
            // Fetch data dari API menggunakan API key dari BuildConfig (tersembunyi dari version control)
            val response = apiService.getNews(BuildConfig.API_KEY)
            val articles = response.articles

            // Transform ArticlesItem dari API jadi NewsEntity untuk database
            // Cek bookmark state untuk preserve bookmark user meskipun data di-refresh
            val newsList = articles.map { article ->
                // Kenapa perlu cek bookmark? Agar berita yang sudah di-bookmark tidak hilang status bookmarknya
                val isBookmarked = newsDao.isNewsBookmarked(article.title)
                NewsEntity(
                    article.title,
                    article.publishedAt,
                    article.urlToImage,
                    article.url,
                    isBookmarked,
                )
            }

            // Hapus berita lama yang tidak di-bookmark untuk hindari data duplikat
            // Kenapa deleteAll? Untuk refresh data tapi tetap simpan bookmark
            newsDao.deleteAll()
            newsDao.insertNews(newsList)

        } catch (e: Exception) {
            // Jika API call gagal, log error dan emit Error state
            // UI akan tetap bisa tampilkan data cache dari database
            Log.d("NewsRepository", "getHeadlineNews: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }

        // Setelah fetch dari API, observe data dari database sebagai source of truth
        // Kenapa emitSource database? Agar UI otomatis update ketika data database berubah
        val localData: LiveData<Result<List<NewsEntity>>> = newsDao.getNews().map { Result.Success(it) }
        emitSource(localData)
    }

    // Mengambil hanya berita yang di-bookmark dari database
    // Tidak perlu fetch dari API karena bookmark adalah data lokal user
    fun getBookmarkedNews(): LiveData<List<NewsEntity>> {
        return newsDao.getBookmarkedNews()
    }

    // Toggle bookmark state dan update ke database
    // Kenapa suspend? Karena operasi database harus di background thread (via coroutine)
    suspend fun setBookmarkedNews(news: NewsEntity, bookmarkState: Boolean) {
        news.isBookmarked = bookmarkState
        newsDao.updateNews(news)
    }

    companion object {
        // Singleton pattern untuk Repository
        // Kenapa singleton? Agar data konsisten di seluruh app dan tidak ada multiple instance
        @Volatile
        private var instance: NewsRepository? = null

        fun getInstance(
            apiService: ApiService,
            newsDao: NewsDao,
            appExecutors: AppExecutors,
        ): NewsRepository = instance ?: synchronized(this) {
            instance ?: NewsRepository(apiService, newsDao, appExecutors)
        }.also { instance = it }
    }
}