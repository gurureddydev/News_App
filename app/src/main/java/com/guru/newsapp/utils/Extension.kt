package com.guru.newsapp.utils

import android.content.Context
import android.widget.Toast
import com.guru.newsapp.model.ArticleX

fun List<ArticleX>.filterByCategory(category: String): List<ArticleX> {
    return this.filter { article -> article.title.contains(category, ignoreCase = true) }
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

}