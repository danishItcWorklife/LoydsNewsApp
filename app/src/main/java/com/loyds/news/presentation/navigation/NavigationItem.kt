package com.loyds.news.presentation.navigation
sealed class NavigationItem(val route: String) {
    object HomeScreenNavigationItem : NavigationItem("news_list_screen")
    object DetailsScreenNavigationItem : NavigationItem("news_details_screen/{newsArticle}")
}
