package com.loyds.news.presentation.intent


sealed class NewsIntent {
    data class FetchNews(val countryCode: String, val category: String, val pageNumber: Int) :
        NewsIntent()
}
