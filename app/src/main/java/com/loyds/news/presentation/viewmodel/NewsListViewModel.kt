package com.loyds.news.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loyds.news.data.model.NewsArticle
import com.loyds.news.data.di.CoroutinesDispatcherProvider
import com.loyds.news.domain.usecase.GetNewsListUseCase
import com.loyds.news.presentation.intent.NewsIntent
import com.loyds.news.state.DataState
import com.loyds.news.utils.Constants
import com.loyds.news.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val getNewsListUseCase: GetNewsListUseCase,
    private val networkHelper: NetworkHelper,
    private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val TAG = "MainViewModel"
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> get() = _errorMessage

    private val _newsResponse = MutableStateFlow<DataState<List<NewsArticle>>>(DataState.Empty())
    val newsResponse: StateFlow<DataState<List<NewsArticle>>> get() = _newsResponse

    private val _intent = MutableSharedFlow<NewsIntent>()
    private val intent: SharedFlow<NewsIntent> get() = _intent

    init {
        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch(coroutinesDispatcherProvider.io) {
            intent.collect { userIntent ->
                when (userIntent) {
                    is NewsIntent.FetchNews -> fetchNews(
                        userIntent.countryCode,
                        userIntent.category,
                        userIntent.pageNumber,
                    )
                }
            }
        }
    }

    fun fetchNews(countryCode: String, category: String, pageNumber: Int) {
        viewModelScope.launch(coroutinesDispatcherProvider.io) {
            _newsResponse.value = DataState.Loading()
            val response = getNewsListUseCase.getNews(
                countryCode,
                category,
                pageNumber
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

    fun sendIntent(intent: NewsIntent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }
}
