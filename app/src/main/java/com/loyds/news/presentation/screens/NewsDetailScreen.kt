package com.loyds.news.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.loyds.news.R
import com.loyds.news.data.model.NewsArticle
import com.loyds.news.utils.Utilities.formatDate

@Composable
fun NewsDetailScreen(newsArticle: NewsArticle) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        newsArticle.urlToImage?.let { urlToImage ->
            loadLargeImage(path = urlToImage)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = newsArticle.title ?: stringResource(id = R.string.no_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        newsArticle.author?.let {
            Text(
                text = "By $it",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        newsArticle.publishedAt?.let {
            Text(
                text = it.formatDate(),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        newsArticle.description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        newsArticle.content?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        newsArticle.url?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        newsArticle.source?.name?.let {
            Text(
                text = "Source: $it",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        newsArticle.category?.let {
            Text(
                text = "Category: $it",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun loadLargeImage(path: String) {
    GlideImage(
        model = path,
        contentDescription = stringResource(id = R.string.load_image),
        contentScale = ContentScale.Crop,
        modifier = Modifier
             .height(290.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        it
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.ic_baseline_newspaper_24)
            .placeholder(R.drawable.ic_baseline_newspaper_24)
            .load(path)
    }
}
