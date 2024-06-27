package com.loyds.news.data.repository


import androidx.room.withTransaction
import com.loyds.news.data.local.NewsDatabase
import com.loyds.news.data.model.NewsArticle
import com.loyds.news.data.network.api.ApiHelper
import com.loyds.news.domain.repository.NewsRepository
import com.loyds.news.state.DataState
import com.loyds.news.utils.NetworkHelper
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val remoteDataSource: ApiHelper,
    private val networkUtil: NetworkHelper,
    private val newsDatabase: NewsDatabase
) : NewsRepository {
    override suspend fun getNews(
        countryCode: String, category: String,
        pageNumber: Int
    ): DataState<List<NewsArticle>> {
        return if (networkUtil.isNetworkConnected()) {
            // When the network is available, fetch data from the remote data source
            try {
                val response = remoteDataSource.getNews(countryCode, category, pageNumber)
                val result = response.body()
                val newsDao = newsDatabase.getNewsDao()
                if (response.isSuccessful && result != null) {
                    if (result.status == "ok") {
                        // Saving news articles to the local database for offline access
                             newsDao.deleteByCategory(category)
                            result.articles.forEach { article ->
                                article.category = category
                                newsDao.upsert(article)
                            }

                        DataState.Success(result.articles)
                    } else {
                        DataState.Error("An error occurred")
                    }
                } else {
                    DataState.Error("An error occurred")
                }
            } catch (e: Exception) {
                DataState.Error("Error occurred ${e.localizedMessage}")
            }
        } else {
            // When the network is not available, fetch data from the local data source
            try {
                val newsDao = newsDatabase.getNewsDao()
                val cachedNews = newsDao.getNewsByCategory(category)
                if (cachedNews.isNotEmpty()) {
                    DataState.Success(cachedNews)
                } else {
                    DataState.Error("No cached news available")
                }
            } catch (e: Exception) {
                DataState.Error("Error occurred ${e.localizedMessage}")
            }
        }
    }

}