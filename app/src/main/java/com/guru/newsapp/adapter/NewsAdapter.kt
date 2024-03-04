package com.guru.newsapp.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.guru.newsapp.R
import com.guru.newsapp.databinding.ItemNewsBinding
import com.guru.newsapp.model.ArticleX
import com.guru.newsapp.utils.FormatDate.formatDate
import com.guru.newsapp.utils.showToast
import java.text.SimpleDateFormat
import java.util.Locale


class NewsAdapter(private var articles: List<ArticleX>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val allArticles: List<ArticleX> = articles.toList()
    private lateinit var binding: ItemNewsBinding

    inner class NewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            setItemClickListener()
        }

        private fun setItemClickListener() {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val article = articles[position]
                    ArticleClickListener.openArticleInBrowser(itemView.context, article.url)
                }
            }
        }

        fun bind(article: ArticleX) {
            binding.apply {
                titleTextView.text = article.title
                descriptionTextView.text = article.description
                authorTextView.text = article.author
                publishedAtTextView.text = formatDate(article.publishedAt)
                Glide.with(itemView)
                    .load(article.urlToImage)
                    .placeholder(R.drawable.img_no)
                    .error(R.drawable.img_no)
                    .into(imageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int {
        return articles.size
    }


    fun filterArticles(query: String) {
        val filteredList = if (query.isEmpty()) {
            // If the query is empty, show all articles
            allArticles
        } else {
            // Filter articles based on the search query
            allArticles.filter { article ->
                // Check if the properties are not null before calling contains
                article.title?.contains(query, ignoreCase = true) == true ||
                        article.description?.contains(query, ignoreCase = true) == true ||
                        (article.author?.contains(query, ignoreCase = true) == true)
            }
        }

        // Update the adapter with the filtered list
        setArticles(filteredList)
    }


    fun setArticles(newArticles: List<ArticleX>) {
        articles = newArticles
        notifyDataSetChanged()
    }

    fun sortByNewToOld() {
        articles = articles.sortedByDescending {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(it.publishedAt)
        }
    }

    fun sortByOldToNew() {
        articles = articles.sortedBy {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(it.publishedAt)
        }
    }
}

object ArticleClickListener {
    fun openArticleInBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            context.showToast("No browser app installed")
        }
    }
}