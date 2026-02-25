package com.dicoding.newsapp.utils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

// Utility object untuk memformat tanggal dari API ke format yang user-friendly
// Kenapa perlu? API mengirim ISO 8601 format yang sulit dibaca, perlu diformat ulang
object DateFormatter {
    fun formatDate(currentDate: String): String? {
        // Format tanggal dari API (ISO 8601): "2024-01-15T10:30:00Z"
        val currentFormat = "yyyy-MM-dd'T'hh:mm:ss'Z'"

        // Format target untuk ditampilkan ke user: "15 Jan 2024 | 10:30"
        // Kenapa format ini? Lebih mudah dibaca dan lebih compact untuk UI
        val targetFormat = "dd MMM yyyy | HH:mm"

        // Timezone GMT karena API mengirim waktu dalam GMT/UTC
        val timezone = "GMT"

        // Parser untuk membaca string date dari API sesuai currentFormat
        val currentDf: DateFormat = SimpleDateFormat(currentFormat, Locale.getDefault())
        currentDf.timeZone = TimeZone.getTimeZone(timezone)

        // Formatter untuk mengubah Date object ke string sesuai targetFormat
        val targetDf: DateFormat = SimpleDateFormat(targetFormat, Locale.getDefault())
        var targetDate: String? = null

        try {
            // Parse string date jadi Date object
            val date = currentDf.parse(currentDate)
            if (date != null) {
                // Format Date object jadi string dengan format target
                targetDate = targetDf.format(date)
            }
        } catch (ex: ParseException) {
            // Jika parsing gagal (format tidak sesuai), log error dan return null
            // UI akan handle null value (tampilkan placeholder atau skip)
            ex.printStackTrace()
        }
        return targetDate
    }
}
