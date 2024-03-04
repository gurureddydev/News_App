package com.guru.newsapp.api

import com.google.gson.Gson
import com.guru.newsapp.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class NewsApiTask(private val listener: OnApiRequestCompletedListener) {

    interface OnApiRequestCompletedListener {
        fun onApiRequestCompleted(result: Article?)
        fun onApiRequestFailed(error: String)
    }

    fun fetchNews(apiUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val jsonResult = makeHttpRequest(apiUrl)
                val article = jsonResult?.let { parseJson(it) }
                launch(Dispatchers.Main) {
                    listener.onApiRequestCompleted(article)
                }
            } catch (e: IOException) {
                launch(Dispatchers.Main) {
                    listener.onApiRequestFailed("Error: ${e.message}")
                }
            }
        }
    }

    @Throws(IOException::class)
    private suspend fun makeHttpRequest(apiUrl: String): String? {
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection
        val response = StringBuilder()

        return try {
            val inputStream = BufferedReader(InputStreamReader(connection.inputStream))
            var line: String?
            while (inputStream.readLine().also { line = it } != null) {
                response.append(line)
            }
            response.toString()
        } finally {
            connection.disconnect()
        }
    }

    private fun parseJson(jsonString: String): Article? {
        // Use Gson for JSON parsing
        return try {
            val gson = Gson()
            gson.fromJson(jsonString, Article::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
