package com.loyds.news.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loyds.news.data.model.NewsArticle
import com.loyds.news.data.di.CoroutinesDispatcherProvider
import com.loyds.news.domain.repository.NewsRepository
import com.loyds.news.state.DataState
import com.loyds.news.utils.Constants
import com.loyds.news.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsDetailsViewModel @Inject constructor(
    private val repository: NewsRepository,
    private val networkHelper: NetworkHelper,
    private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val TAG = "MainViewModel"
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> get() = _errorMessage

    private val _newsResponse = MutableStateFlow<DataState<List<NewsArticle>>>(DataState.Empty())
    val newsResponse: StateFlow<DataState<List<NewsArticle>>> get() = _newsResponse


    init {
        fetchNews(Constants.CountryCode, Constants.Category)
    }

    fun fetchNews(countryCode: String, category: String) {
        viewModelScope.launch(coroutinesDispatcherProvider.io) {
            _newsResponse.value = DataState.Loading()
            val response = repository.getNews(
                countryCode,
                category,
                Constants.DEFAULT_PAGE_INDEX
            )
            when (response) {
                is DataState.Success -> {
                    _newsResponse.value = handleFeedNewsResponse(response)
                 }

                is DataState.Error -> {
                    _newsResponse.value = DataState.Error(response.message ?: "Error")
                }

                else -> {
                    // Handle other cases if needed
                }
            }
        }
    }

private fun handleFeedNewsResponse(response: DataState<List<NewsArticle>>): DataState<List<NewsArticle>> {
     return response
}

}
