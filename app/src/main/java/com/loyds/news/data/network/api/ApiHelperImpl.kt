package com.loyds.news.data.network.api

import com.loyds.news.data.model.NewsResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val newsApi: NewsApi) : ApiHelper {

    override suspend fun getNews(
        countryCode: String,
        category: String,
        pageNumber: Int
    ): Response<NewsResponse> =
        newsApi.getNews(countryCode, category, pageNumber)

}