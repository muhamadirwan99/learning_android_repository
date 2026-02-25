package com.dicoding.newsapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity class yang merepresentasikan tabel "news" di Room Database
// Kenapa diperlukan? Untuk menyimpan data berita secara lokal agar bisa diakses offline
@Entity(tableName = "news")
class NewsEntity(
    // Title sebagai primary key karena setiap berita punya judul unik
    // Kenapa title jadi PK? Karena tidak ada ID unik dari API, dan title bisa jadi identifier
    @field:ColumnInfo(name = "title")
    @field:PrimaryKey
    val title: String,

    // Waktu publikasi untuk mengurutkan berita (terbaru di atas)
    @field:ColumnInfo(name = "publishedAt")
    val publishedAt: String,

    // URL gambar nullable karena tidak semua berita punya gambar
    @field:ColumnInfo(name = "urlToImage")
    val urlToImage: String? = null,

    // URL artikel nullable untuk membuka berita di browser
    @field:ColumnInfo(name = "url")
    val url: String? = null,

    // Flag bookmark menggunakan var (mutable) karena user bisa toggle bookmark kapan saja
    // Kenapa var? Karena state bookmark harus bisa berubah tanpa membuat object baru
    @field:ColumnInfo(name = "bookmarked")
    var isBookmarked: Boolean
)