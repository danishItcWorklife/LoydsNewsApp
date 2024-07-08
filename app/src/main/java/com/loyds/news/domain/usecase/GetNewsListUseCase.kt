package com.loyds.news.domain.usecase

import com.loyds.news.data.model.NewsArticle
import com.loyds.news.domain.repository.NewsRepository
import com.loyds.news.state.DataState
import javax.inject.Inject

class GetNewsListUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend fun getNews(
        countryCode: String,
        category: String,
        pageNumber: Int
    ): DataState<List<NewsArticle>> {
        return repository.getNews(countryCode, category, pageNumber)
    }
}
