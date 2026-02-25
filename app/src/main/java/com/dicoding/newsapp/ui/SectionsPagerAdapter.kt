package com.dicoding.newsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

// Adapter untuk ViewPager2 yang menampilkan 2 tab: News dan Bookmark
// Kenapa FragmentStateAdapter? Untuk efficiently manage multiple fragments dengan lifecycle management
class SectionsPagerAdapter internal constructor(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        // Buat instance NewsFragment untuk setiap tab
        val fragment = NewsFragment()
        val bundle = Bundle()

        // Pass argument ke fragment untuk membedakan tab News vs Bookmark
        // Kenapa Bundle? Untuk komunikasi data ke Fragment dengan cara yang recommended (survive config changes)
        if (position == 0) {
            // Tab pertama (index 0) untuk menampilkan headline news
            bundle.putString(NewsFragment.ARG_TAB, NewsFragment.TAB_NEWS)
        } else {
            // Tab kedua (index 1) untuk menampilkan bookmarked news
            bundle.putString(NewsFragment.ARG_TAB, NewsFragment.TAB_BOOKMARK)
        }
        fragment.arguments = bundle
        return fragment
    }

    // Total jumlah tab dalam ViewPager
    // Return 2 karena ada 2 tab: News dan Bookmark
    override fun getItemCount(): Int {
        return 2
    }
}