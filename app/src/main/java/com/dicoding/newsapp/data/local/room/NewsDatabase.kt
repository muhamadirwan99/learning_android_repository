package com.dicoding.newsapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.newsapp.data.local.entity.NewsEntity

// Database class untuk Room - mendefinisikan entities dan versi database
// version = 1 untuk database baru, naikkan jika ada perubahan skema (migration)
@Database(entities = [NewsEntity::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {
    // Abstract function untuk mendapatkan DAO - Room akan implement otomatis
    abstract fun newsDao(): NewsDao

    companion object {
        // @Volatile agar perubahan instance langsung visible ke semua thread
        // Kenapa? Untuk menghindari race condition di multi-threading
        @Volatile
        private var instance: NewsDatabase? = null

        // Singleton pattern dengan double-check locking untuk efisiensi
        // Kenapa singleton? Agar hanya ada 1 instance database untuk seluruh app (hemat memori)
        fun getInstance(context: Context): NewsDatabase =
            instance ?: synchronized(this) {
                // Double-check: cek lagi setelah masuk synchronized block
                // Kenapa? Thread lain mungkin sudah create instance saat waiting
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java, "News.db"
                ).build()
            }
    }
}