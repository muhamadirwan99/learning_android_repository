package com.dicoding.newsapp.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

// Class untuk mengelola thread executors (background dan main thread operations)
// Kenapa perlu? Untuk operasi database/network yang tidak boleh di main thread (avoid ANR)
class AppExecutors {
    // Single thread executor untuk operasi disk/database - sequential execution
    // Kenapa single thread? Database operation harus sequential untuk avoid race condition
    val diskIO: Executor = Executors.newSingleThreadExecutor()

    // Thread pool dengan 3 thread untuk network operations - bisa parallel
    // Kenapa 3 thread? Balance antara performance dan resource usage untuk multiple network calls
    val networkIO: Executor = Executors.newFixedThreadPool(3)

    // Executor untuk post result ke main thread (update UI)
    val mainThread: Executor = MainThreadExecutor()

    // Custom executor untuk menjalankan task di main thread menggunakan Handler
    // Kenapa perlu? Karena UI update harus dilakukan di main thread
    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            // Post runnable ke main thread message queue
            mainThreadHandler.post(command)
        }
    }

}