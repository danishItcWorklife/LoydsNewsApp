package com.loyds.news.domain.repository

import com.loyds.news.data.model.NewsArticle
import com.loyds.news.state.DataState


interface NewsRepository {
    suspend fun getNews(
        countryCode: String,
        category: String,
        pageNumber: Int
    ): DataState<List<NewsArticle>>

}
