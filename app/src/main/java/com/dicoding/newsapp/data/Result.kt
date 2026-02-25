package com.dicoding.newsapp.data

// Sealed class untuk membungkus berbagai kemungkinan state dari operasi async (API/Database)
// Kenapa sealed class? Agar compiler bisa memastikan semua kemungkinan state sudah ditangani (exhaustive when)
sealed class Result<out R> private constructor() {
    // State ketika data berhasil dimuat - membawa data hasil operasi
    data class Success<out T>(val data: T) : Result<T>()

    // State ketika terjadi error - membawa pesan error untuk ditampilkan ke user
    data class Error(val error: String) : Result<Nothing>()

    // State ketika proses loading sedang berjalan - untuk menampilkan progress indicator
    object Loading : Result<Nothing>()
}