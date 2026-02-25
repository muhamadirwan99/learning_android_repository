package com.dicoding.newsapp.data.remote.response

import com.google.gson.annotations.SerializedName

// Data class untuk menerima response JSON dari News API
// Kenapa @SerializedName? Untuk mapping field JSON ke property Kotlin yang mungkin beda nama
data class NewsResponse(
    @field:SerializedName("totalResults")
    val totalResults: Int,

    // List artikel yang akan di-parse jadi NewsEntity untuk disimpan ke database
    @field:SerializedName("articles")
    val articles: List<ArticlesItem>,

    @field:SerializedName("status")
    val status: String
)

// Data class untuk info sumber berita
data class Source(
    @field:SerializedName("name")
    val name: String,

    // Type Any karena id bisa null atau string, tergantung sumber
    @field:SerializedName("id")
    val id: Any
)

// Data class merepresentasikan 1 artikel berita dari API
// Kenapa tidak langsung pakai NewsEntity? Karena struktur API berbeda dengan struktur database kita
data class ArticlesItem(
    // Waktu publish dalam format ISO 8601, perlu di-format ulang untuk ditampilkan
    @field:SerializedName("publishedAt")
    val publishedAt: String,

    @field:SerializedName("author")
    val author: String,

    // URL gambar untuk ditampilkan dengan Glide
    @field:SerializedName("urlToImage")
    val urlToImage: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("source")
    val source: Source,

    @field:SerializedName("title")
    val title: String,

    // URL artikel untuk dibuka di browser ketika item di-klik
    @field:SerializedName("url")
    val url: String,

    // Type Any karena content bisa berbagai format
    @field:SerializedName("content")
    val content: Any
)
