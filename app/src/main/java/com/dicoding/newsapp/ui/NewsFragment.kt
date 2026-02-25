package com.dicoding.newsapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.newsapp.data.Result
import com.dicoding.newsapp.databinding.FragmentNewsBinding

// Fragment yang menampilkan daftar berita (News atau Bookmark tergantung tab)
// Kenapa Fragment? Untuk reusable component yang bisa di-attach ke ViewPager
class NewsFragment : Fragment() {

    // Nullable property untuk tab name dari arguments
    private var tabName: String? = null

    // Nullable binding dengan backing property untuk avoid memory leak
    // Kenapa nullable? Karena binding hanya exist antara onCreateView dan onDestroyView
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate layout menggunakan view binding
        _binding = FragmentNewsBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Ambil tab name dari arguments yang dikirim SectionsPagerAdapter
        tabName = arguments?.getString(ARG_TAB)

        // Dapatkan ViewModelFactory dengan dependency injection
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())

        // Inisialisasi ViewModel menggunakan factory (by viewModels delegate)
        // Kenapa by viewModels? Untuk lifecycle-aware ViewModel yang survive config changes
        val viewModel: NewsViewModel by viewModels {
            factory
        }

        // Setup adapter dengan lambda untuk handle bookmark click
        // Lambda ini akan dipanggil ketika user klik icon bookmark
        val newsAdapter = NewsAdapter { news ->
            // Toggle bookmark: jika sudah di-bookmark -> delete, jika belum -> save
            // Kenapa cek isBookmarked? Untuk menentukan action yang tepat (add/remove bookmark)
            if (news.isBookmarked) {
                viewModel.deleteNews(news)
            } else {
                viewModel.saveNews(news)
            }
        }


        // Logic berbeda berdasarkan tab yang sedang aktif
        if (tabName == TAB_NEWS) {
            // Tab News: observe headline news dari API/Database
            viewModel.getHeadlineNews().observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        // State Loading: tampilkan progress bar
                        is Result.Loading -> {
                            binding?.progressBar?.visibility = View.VISIBLE
                        }

                        // State Success: sembunyikan progress bar dan tampilkan data
                        is Result.Success -> {
                            binding?.progressBar?.visibility = View.GONE
                            val newsData = result.data
                            // Submit list ke adapter untuk ditampilkan di RecyclerView
                            newsAdapter.submitList(newsData)
                        }

                        // State Error: sembunyikan progress bar dan tampilkan error message
                        is Result.Error -> {
                            binding?.progressBar?.visibility = View.GONE
                            Toast.makeText(
                                context,
                                "Terjadi kesalahan" + result.error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        } else if (tabName == TAB_BOOKMARK) {
            // Tab Bookmark: observe bookmarked news dari database saja (tidak perlu fetch API)
            // Kenapa tidak perlu Loading state? Karena data langsung dari local database (cepat)
            viewModel.getBookmarkedNews().observe(viewLifecycleOwner) { bookmarkedNews ->
                binding?.progressBar?.visibility = View.GONE
                newsAdapter.submitList(bookmarkedNews)
            }
        }

        // Setup RecyclerView dengan LinearLayoutManager dan adapter
        binding?.rvNews?.apply {
            layoutManager = LinearLayoutManager(context)
            // setHasFixedSize untuk optimasi karena RecyclerView size tidak berubah
            setHasFixedSize(true)
            adapter = newsAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Set binding ke null untuk avoid memory leak
        // Kenapa perlu? Karena binding memegang reference ke View yang bisa leak
        _binding = null
    }

    companion object {
        // Constant untuk argument key dan tab values
        const val ARG_TAB = "tab_name"
        const val TAB_NEWS = "news"
        const val TAB_BOOKMARK = "bookmark"
    }
}