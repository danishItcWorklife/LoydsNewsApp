package com.loyds.news

import com.loyds.news.data.model.NewsArticle
import com.loyds.news.domain.repository.NewsRepository
import com.loyds.news.domain.usecase.GetNewsListUseCase
import com.loyds.news.state.DataState
import com.loyds.news.utils.Constants
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations


class GetNewsListUseCaseTest {
    // Mock the repository implementation

    private val newsRepository: NewsRepository = mock()
    private lateinit var getNewsListUseCase: GetNewsListUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getNewsListUseCase = GetNewsListUseCase(newsRepository)
    }


    @Test
    fun `when getNews is called with valid inputs, it returns success`() {
        // Mock data and expected behavior
        val countryCode = Constants.CountryCode
        val category = Constants.Category
        val pageNumber = Constants.DEFAULT_PAGE_INDEX

        val fakeArticles = FakeDataUtil.getFakeArticles().toList()

        val expectedResponse = DataState.Success(fakeArticles)

        // Mock the repository method to return the expected response
        runBlocking {
            whenever(newsRepository.getNews(anyString(), anyString(), anyInt())).thenReturn(
                    expectedResponse
                )
        }

        // Call the method under test
        val actualResponse = runBlocking {
            getNewsListUseCase.getNews(countryCode, category, pageNumber)
        }

        // Assert that the actual response matches the expected response
        Assert.assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `when getNews is called with invalid inputs, it returns error`() = runBlocking {
        // Arrange
        whenever(
            newsRepository.getNews(
                Constants.CountryCode, Constants.Category, Constants.DEFAULT_PAGE_INDEX
            )
        ).thenAnswer { DataState.Error("Error occurred", null) }

        // Call the method under test
        val actualResponse = runBlocking {
            getNewsListUseCase.getNews(
                Constants.CountryCode, Constants.Category, Constants.DEFAULT_PAGE_INDEX
            )
        }

        // Assert
        assert(actualResponse is DataState.Error)

    }

    @Test
    fun `when getNews is called and repository returns loading, it returns loading`(): Unit =
        runBlocking {
            // Arrange
            val countryCode = Constants.CountryCode
            val category = Constants.Category
            val pageNumber = Constants.DEFAULT_PAGE_INDEX
            val expectedResponse = DataState.Loading<List<NewsArticle>>()

            whenever(newsRepository.getNews(anyString(), anyString(), anyInt())).thenReturn(
                    expectedResponse
                )

            // Act
            val actualResponse = getNewsListUseCase.getNews(countryCode, category, pageNumber)

            // Assert
            assertEquals(expectedResponse, actualResponse)
            verify(newsRepository).getNews(countryCode, category, pageNumber)
        }
}
