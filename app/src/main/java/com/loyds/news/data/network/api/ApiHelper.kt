package com.loyds.news.data.network.api

import com.loyds.news.data.model.NewsResponse
import retrofit2.Response

interface ApiHelper {
     suspend fun getNews(countryCode: String,category: String, pageNumber: Int): Response<NewsResponse>
}