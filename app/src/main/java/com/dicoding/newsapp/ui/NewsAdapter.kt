package com.dicoding.newsapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.newsapp.R
import com.dicoding.newsapp.data.local.entity.NewsEntity
import com.dicoding.newsapp.databinding.ItemNewsBinding
import com.dicoding.newsapp.ui.NewsAdapter.MyViewHolder
import com.dicoding.newsapp.utils.DateFormatter

// Adapter untuk RecyclerView yang menampilkan list berita
// Kenapa ListAdapter? Karena sudah include DiffUtil untuk efisiensi update (tidak re-render semua item)
class NewsAdapter(private val onBookmarkClick: (NewsEntity) -> Unit) :
    ListAdapter<NewsEntity, MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate layout item_news untuk setiap item di RecyclerView
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Bind data berita ke ViewHolder untuk item di posisi tertentu
        val news = getItem(position)
        holder.bind(news)

        // Handle bookmark icon berdasarkan state isBookmarked
        val ivBookmark = holder.binding.ivBookmark
        if (news.isBookmarked) {
            // Tampilkan icon filled jika sudah di-bookmark
            ivBookmark.setImageDrawable(ContextCompat.getDrawable(ivBookmark.context, R.drawable.ic_bookmarked_white))
        } else {
            // Tampilkan icon outline jika belum di-bookmark
            ivBookmark.setImageDrawable(ContextCompat.getDrawable(ivBookmark.context, R.drawable.ic_bookmark_white))
        }

        // Set click listener untuk toggle bookmark
        // Lambda onBookmarkClick akan dipanggil dengan news entity sebagai parameter
        ivBookmark.setOnClickListener {
            onBookmarkClick(news)
        }
    }

    // ViewHolder untuk menyimpan reference ke views dalam item layout
    // Kenapa ViewHolder? Untuk menghindari findViewById berulang kali (performance optimization)
    class MyViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(news: NewsEntity) {
            // Bind data berita ke views
            binding.tvItemTitle.text = news.title

            // Format tanggal dari ISO 8601 ke format yang lebih readable
            binding.tvItemPublishedDate.text = DateFormatter.formatDate(news.publishedAt)

            // Load image menggunakan Glide dengan placeholder dan error image
            // Kenapa Glide? Untuk efficient image loading, caching, dan memory management
            Glide.with(itemView.context)
                .load(news.urlToImage)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
                .into(binding.imgPoster)

            // Set click listener untuk membuka artikel di browser
            // Kenapa implicit intent? Agar user bisa pilih browser favoritnya
            itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(news.url)
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        // DiffUtil callback untuk menghitung perubahan list secara efisien
        // Kenapa DiffUtil? Agar RecyclerView hanya update item yang berubah, bukan semua item
        val DIFF_CALLBACK: DiffUtil.ItemCallback<NewsEntity> =
            object : DiffUtil.ItemCallback<NewsEntity>() {
                // Cek apakah dua item merepresentasikan entity yang sama (by unique identifier)
                // Kenapa title? Karena title adalah primary key di database
                override fun areItemsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
                    return oldItem.title == newItem.title
                }

                // Cek apakah content dari item sama (untuk determine perlu update atau tidak)
                // @SuppressLint karena equals() sudah adequate untuk data class
                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
