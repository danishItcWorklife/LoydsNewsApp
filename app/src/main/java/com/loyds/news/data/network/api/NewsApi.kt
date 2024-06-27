package com.loyds.news.data.network.api

import com.loyds.news.BuildConfig
import com.loyds.news.data.model.NewsResponse
import com.loyds.news.utils.Constants.Companion.Category
import com.loyds.news.utils.Constants.Companion.CountryCode
import com.loyds.news.utils.Constants.Companion.DEFAULT_PAGE_INDEX
import com.loyds.news.utils.Constants.Companion.QUERY_PER_PAGE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getNews(
        @Query("country")
        countryCode: String = CountryCode,
        @Query("category")
        category: String = Category,
        @Query("page")
        pageNumber: Int = DEFAULT_PAGE_INDEX,
        @Query("pageSize")
        pageSize: Int = QUERY_PER_PAGE,
        @Query("apiKey")
        apiKey: String = BuildConfig.API_KEY
    ): Response<NewsResponse>
}