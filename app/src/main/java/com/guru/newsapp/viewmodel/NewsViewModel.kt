package com.guru.newsapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.guru.newsapp.model.ArticleX

class NewsViewModel : ViewModel() {
    private val _articles = MutableLiveData<List<ArticleX>>()
    val articles: LiveData<List<ArticleX>> get() = _articles

    fun setArticles(newArticles: List<ArticleX>) {
        _articles.value = newArticles
    }
}
