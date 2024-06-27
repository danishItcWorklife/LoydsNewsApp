package com.loyds.news.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.loyds.news.utils.Constants
import java.io.Serializable

@Entity(
    tableName = Constants.TB_NAME
)
data class NewsArticle(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") var id: Int? = null,
    @SerializedName("author") val author: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("publishedAt") var publishedAt: String?,
    @SerializedName("source") val source: Source?,
    @SerializedName("title") val title: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("urlToImage") val urlToImage: String?,
    @SerializedName("category") var category: String? = ""
) : Serializable
