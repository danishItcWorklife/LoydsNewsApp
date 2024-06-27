package com.loyds.news.data.di


import android.content.Context
import com.loyds.news.data.local.NewsDao
import com.loyds.news.data.local.NewsDatabase
import com.loyds.news.data.network.api.ApiHelper
import com.loyds.news.domain.repository.NewsRepository
import com.loyds.news.data.repository.NewsRepositoryImpl
import com.loyds.news.utils.NetworkHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        NewsDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideNewsDao(db: NewsDatabase) = db.getNewsDao()

    @Singleton
    @Provides
    fun provideRepository(
        remoteDataSource: ApiHelper,
        localDataSource: NewsDao,
        networkHelper: NetworkHelper,
    ) = NewsRepositoryImpl(remoteDataSource, networkHelper, localDataSource)

    @Singleton
    @Provides
    fun provideINewsRepository(repository: NewsRepositoryImpl): NewsRepository = repository
}