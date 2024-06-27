package com.loyds.news.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.loyds.news.R
import com.loyds.news.data.model.NewsArticle
import com.loyds.news.state.DataState
import com.loyds.news.presentation.viewmodel.NewsListViewModel
import com.loyds.news.utils.Constants
import com.loyds.news.utils.Utilities
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Locale

@Composable
fun NewsListScreen(
    navController: NavController,
    newsListViewModel: NewsListViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val newsState by newsListViewModel.newsResponse.collectAsState()
    val errorMessage by newsListViewModel.errorMessage.collectAsState()
    val categories =
        Utilities.getStringArray(context, R.array.news_categories)
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    NewsListScreen(
        newsState = newsState,
        errorMessage = errorMessage,
        categories = categories,
        selectedCategory = selectedCategory,
        navController = navController,
        onCategorySelected = { category ->
            selectedCategory = category
            newsListViewModel.fetchNews(
                Constants.CountryCode,
                category.lowercase(Locale.getDefault())
            )
        }
    )
}

@Composable
fun NewsListScreen(
    newsState: DataState<List<NewsArticle>>,
    errorMessage: String,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    navController: NavController
) {
    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Scrollable row for categories
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp) // Fixed height for the category row
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    content = {
                        items(categories) { category ->
                            CategoryItem(
                                category = category,
                                isSelected = category == selectedCategory,
                                onClick = { onCategorySelected(category) }
                            )
                        }
                    }
                )

                // Scrollable column for news articles
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    when (newsState) {
                        is DataState.Loading -> {
                            item {
                                Text(text = stringResource(id = R.string.loading))
                            }
                        }

                        is DataState.Success -> {
                            newsState.data?.let { articles ->
                                items(articles) { article ->
                                    NewsArticleItem(article = article) { newsId ->
                                        val gson = Gson()
                                        val myObjectString =
                                            gson.toJson(newsId, NewsArticle::class.java)
                                        val encode = URLEncoder.encode(
                                            myObjectString,
                                            StandardCharsets.UTF_8.toString()
                                        )
                                        navController.navigate("news_details_screen/$encode")
                                    }
                                }
                            }
                        }

                        is DataState.Error -> {
                            item {
                                Text(text = stringResource(R.string.error) + " ${newsState.message}")
                            }
                        }

                        is DataState.Empty -> {
                            item {
                                Text(text = stringResource(id = R.string.no_news_available))
                            }
                        }
                    }

                    if (errorMessage.isNotEmpty()) {
                        item {
                            Text(text = errorMessage)
                        }
                    }
                }
            }
        }
    )
}


@Composable
fun CategoryItem(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Text(
        text = category,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = contentColor
    )
}

@Composable
fun NewsArticleItem(article: NewsArticle, onClick: (NewsArticle) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                color = Color.Black.copy(alpha = 0.03f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                onClick.invoke(article)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            loadImage(path = article.urlToImage ?: "")
            // Title and description
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = article.title ?: stringResource(id = R.string.no_title),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = article.description ?: stringResource(id = R.string.no_description),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun loadImage(path: String) {
    GlideImage(
        model = path,
        contentDescription = stringResource(id = R.string.load_image),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .width(90.dp)
            .height(90.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        it
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.ic_baseline_newspaper_24)
            .placeholder(R.drawable.ic_baseline_newspaper_24)
            .load(path)
    }
}
