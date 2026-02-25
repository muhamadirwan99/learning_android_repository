package com.dicoding.newsapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dicoding.newsapp.data.local.entity.NewsEntity

// Data Access Object untuk mengakses tabel news di database
// Kenapa interface? Room akan auto-generate implementasinya di compile time
@Dao
interface NewsDao {
    // Mengambil semua berita diurutkan dari yang terbaru (DESC)
    // LiveData agar UI otomatis update ketika data di database berubah
    @Query("SELECT * FROM news ORDER BY publishedAt DESC")
    fun getNews(): LiveData<List<NewsEntity>>

    // Mengambil hanya berita yang di-bookmark (bookmarked = 1)
    // Kenapa perlu query terpisah? Agar tab bookmark hanya tampilkan berita yang disimpan user
    @Query("SELECT * FROM news where bookmarked = 1")
    fun getBookmarkedNews(): LiveData<List<NewsEntity>>

    // Insert berita dari API, IGNORE jika sudah ada (berdasarkan primary key)
    // Kenapa IGNORE? Agar berita yang sudah di-bookmark tidak ter-overwrite
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNews(news: List<NewsEntity>)

    // Update berita individual, digunakan saat user toggle bookmark
    // Kenapa update? Karena hanya mengubah status bookmark, bukan insert data baru
    @Update
    suspend fun updateNews(news: NewsEntity)

    // Hapus berita yang tidak di-bookmark sebelum refresh data baru dari API
    // Kenapa bookmarked = 0? Agar berita yang di-bookmark tetap tersimpan meski di-refresh
    @Query("DELETE FROM news WHERE bookmarked = 0")
    suspend fun deleteAll()

    // Cek apakah berita sudah di-bookmark sebelumnya (untuk preserve state saat refresh)
    // Kenapa perlu EXISTS? Untuk return boolean true/false secara efisien
    @Query("SELECT EXISTS(SELECT * FROM news WHERE title = :title AND bookmarked = 1)")
    suspend fun isNewsBookmarked(title: String): Boolean
}