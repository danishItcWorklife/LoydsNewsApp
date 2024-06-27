package com.loyds.news.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.loyds.news.data.model.NewsArticle
import com.loyds.news.presentation.screens.NewsDetailScreen
import com.loyds.news.presentation.screens.NewsListScreen
import com.loyds.news.utils.Utilities
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavigationItem.HomeScreenNavigationItem.route
    ) {
        composable(NavigationItem.HomeScreenNavigationItem.route) {
            NewsListScreen(navController)
        }

        composable(
            NavigationItem.DetailsScreenNavigationItem.route,
            arguments = listOf(navArgument("newsArticle") {
                type = NavType.StringType
            })
        ) {
            it.arguments?.getString("newsArticle")?.let { jsonString ->
                val gson = Gson()
                val decoded = Utilities.safeDecode(
                    jsonString
                 )
                val newsArticle = gson.fromJson(decoded, NewsArticle::class.java)
                NewsDetailScreen(newsArticle = newsArticle)
            }
        }
    }
}