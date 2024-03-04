package com.guru.newsapp.model

data class ArticleX(
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source,
    val title: String,
    val url: String,
    val urlToImage: String,
    var isBookmarked: Boolean = false
)