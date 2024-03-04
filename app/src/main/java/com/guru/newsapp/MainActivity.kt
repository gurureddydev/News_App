package com.guru.newsapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.guru.newsapp.adapter.NewsAdapter
import com.guru.newsapp.api.NewsApiTask
import com.guru.newsapp.databinding.ActivityMainBinding
import com.guru.newsapp.model.Article
import com.guru.newsapp.utils.AppConfig.API_URL
import com.guru.newsapp.utils.filterByCategory
import com.guru.newsapp.utils.showToast
import com.guru.newsapp.viewmodel.NewsViewModel

class MainActivity : AppCompatActivity(), NewsApiTask.OnApiRequestCompletedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsViewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        newsViewModel = ViewModelProvider(this)[NewsViewModel::class.java]

        // RecyclerView setup
        setupRecyclerView()

        // ChipGroup setup
        setupFilterChips()

        // News API request
        val newsApiTask = NewsApiTask(this)
        showProgressBar()
        newsApiTask.fetchNews(API_URL)

        // Search EditText setup
        setupSearchEditText()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(emptyList())
        binding.recyclerView.adapter = newsAdapter
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun setupFilterChips() {
        val categories = resources.getStringArray(R.array.news_categories)

        for (category in categories) {
            val chip = Chip(this)
            chip.text = category
            chip.isCheckable = true
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (category == getString(R.string.all_category)) {
                        showAllNews()
                    } else {
                        filterByCategory(category)
                    }
                }
            }

            binding.chipGroup.addView(chip)
        }
    }

    private fun showAllNews() {
        newsAdapter.setArticles(newsViewModel.articles.value.orEmpty())
    }

    private fun filterByCategory(category: String) {
        val filteredList = newsViewModel.articles.value.orEmpty().filterByCategory(category)
        newsAdapter.setArticles(filteredList)
    }

    private fun setupSearchEditText() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this example
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                newsAdapter.filterArticles(query)
            }

            override fun afterTextChanged(s: Editable?) {
                // Not needed for this example
            }
        })
    }

    override fun onApiRequestCompleted(result: Article?) {
        result?.let {
            val articles = it.articles
            newsViewModel.setArticles(articles)
            newsAdapter.setArticles(articles)
        }
        hideProgressBar()
    }

    override fun onApiRequestFailed(error: String) {
        showToast(getString(R.string.error_message, error))
        hideProgressBar()
    }
}
