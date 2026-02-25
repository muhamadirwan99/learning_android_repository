package com.dicoding.newsapp.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.newsapp.R
import com.dicoding.newsapp.databinding.ActivityHomeBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.core.graphics.drawable.toDrawable

// Activity utama yang menampilkan TabLayout dengan ViewPager2
// Kenapa ViewPager2? Untuk swipe gesture antara tab News dan Bookmark
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // View binding untuk akses view tanpa findViewById (type-safe dan null-safe)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup adapter untuk ViewPager2 dengan 2 fragment (News & Bookmark)
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        binding.viewPager.adapter = sectionsPagerAdapter

        // TabLayoutMediator untuk sinkronisasi TabLayout dengan ViewPager2
        // Kenapa Mediator? Untuk auto-handle tab selection dan page scrolling (two-way binding)
        TabLayoutMediator(
            binding.tabs, binding.viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            // Set text untuk setiap tab dari string resources
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        // Hilangkan shadow di ActionBar untuk flat design
        supportActionBar?.elevation = 0f

        // Ambil primary color dari theme untuk set background ActionBar
        // Kenapa dari theme? Agar warna konsisten dengan app theme (support dark mode)
        val typedValue = TypedValue()
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)
        val primaryColor = typedValue.data

        // Set background color ActionBar sesuai primary color
        supportActionBar?.setBackgroundDrawable(primaryColor.toDrawable())

    }

    companion object {
        // Array string resource IDs untuk judul tab
        // Kenapa @StringRes? Untuk support multi-language dari strings.xml
        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.home, R.string.bookmark)
    }
}