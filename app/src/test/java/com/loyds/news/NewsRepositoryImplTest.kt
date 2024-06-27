package com.loyds.news

import FakeDataUtil
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.loyds.news.data.local.NewsDao
import com.loyds.news.data.local.NewsDatabase
import com.loyds.news.data.model.NewsResponse
import com.loyds.news.data.network.api.ApiHelper
import com.loyds.news.data.repository.NewsRepositoryImpl
import com.loyds.news.state.DataState
import com.loyds.news.utils.Constants
import com.loyds.news.utils.NetworkHelper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class NewsRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()

    private lateinit var newsRepositoryImpl: NewsRepositoryImpl
    private val newsDao: NewsDao = mockk()
    private val remoteDataSource: ApiHelper = mockk()
    private val networkUtil: NetworkHelper = mockk()
    private val newsDatabase: NewsDatabase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        every { newsDatabase.getNewsDao() } returns newsDao
        newsRepositoryImpl = NewsRepositoryImpl(remoteDataSource, networkUtil, newsDatabase)
     }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getNews network available success response`() = runTest {


        // Mock network status
        coEvery { networkUtil.isNetworkConnected() } returns true

        // Mock API response
        val articles = FakeDataUtil.getFakeArticles()
        val response = NewsResponse(status = "ok", articles = articles, totalResults = 2)
        coEvery { remoteDataSource.getNews(any(), any(), any()) } returns Response.success(response)
        coEvery { newsDao.deleteByCategory(Constants.Category) } returns Unit
        // Mock local database operation
        coEvery { newsDao.upsert(any()) } returns Unit

        // Call the method
        val result = newsRepositoryImpl.getNews(
            Constants.CountryCode,
            Constants.Category,
            Constants.DEFAULT_PAGE_INDEX
        )

        // Verify interactions and result
        coVerify {
            remoteDataSource.getNews(
                Constants.CountryCode,
                Constants.Category,
                Constants.DEFAULT_PAGE_INDEX
            )
        }
        coVerify { newsDao.upsert(any()) }
        assert(result is DataState.Success && result.data == articles)
    }

    @Test
    fun `getNews network available error response`() = runTest {
        // Mock network status
        coEvery { networkUtil.isNetworkConnected() } returns true

        // Mock API responses
        coEvery { remoteDataSource.getNews(any(), any(), any()) } returns Response.error(
            400,
            mockk(relaxed = true)
        )

        // Call the method
        val result = newsRepositoryImpl.getNews(
            Constants.CountryCode,
            Constants.Category,
            Constants.DEFAULT_PAGE_INDEX
        )

        // Verify interactions and result
        coVerify {
            remoteDataSource.getNews(
                Constants.CountryCode,
                Constants.Category,
                Constants.DEFAULT_PAGE_INDEX
            )
        }
        assert(result is DataState.Error)
    }

    @Test
    fun `getNews network unavailable cached news available`() = runTest {


        // Mock network status
        coEvery { networkUtil.isNetworkConnected() } returns false

        // Mock local database operation
        val cachedArticles = FakeDataUtil.getFakeArticles()
        coEvery { newsDao.getNewsByCategory(any()) } returns cachedArticles

        // Call the method
        val result = newsRepositoryImpl.getNews(
            Constants.CountryCode,
            Constants.Category,
            Constants.DEFAULT_PAGE_INDEX
        )

        // Verify interactions and result
        coVerify { newsDao.getNewsByCategory(Constants.Category) }
        assert(result is DataState.Success && result.data == cachedArticles)
    }

    @Test
    fun `getNews network unavailable no cached news`() = runTest {


        // Mock network status
        coEvery { networkUtil.isNetworkConnected() } returns false

        // Mock local database operation
        coEvery { newsDao.getNewsByCategory(any()) } returns emptyList()

        // Call the method
        val result = newsRepositoryImpl.getNews(
            Constants.CountryCode,
            Constants.Category,
            Constants.DEFAULT_PAGE_INDEX
        )

        // Verify interactions and result
        coVerify { newsDao.getNewsByCategory(Constants.Category) }
        assert(result is DataState.Error)
    }
}
