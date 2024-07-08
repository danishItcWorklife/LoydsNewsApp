import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.loyds.news.data.network.api.NewsApi
import com.loyds.news.domain.usecase.GetNewsListUseCase
import com.loyds.news.presentation.intent.NewsIntent
import com.loyds.news.state.DataState
import com.loyds.news.presentation.viewmodel.NewsListViewModel
import com.loyds.news.utils.Constants.Companion.Category
import com.loyds.news.utils.Constants.Companion.CountryCode
import com.loyds.news.utils.Constants.Companion.DEFAULT_PAGE_INDEX
import com.loyds.news.utils.NetworkHelper
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class NewsListViewModelTest {
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Main coroutines dispatcher for unit testing.
    @get:Rule
    var coroutineRule = MainCoroutineRule()


    @Mock
    private lateinit var getNewsListUseCase: GetNewsListUseCase

    private val testDispatcher = coroutineRule.testDispatcher

    private lateinit var viewModel: NewsListViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = NewsListViewModel(
            getNewsListUseCase = getNewsListUseCase,
             coroutinesDispatcherProvider = provideFakeCoroutinesDispatcherProvider(testDispatcher)
        )
    }

    @Test
    fun `when calling for results then return loading state`() {
        coroutineRule.runBlockingTest {
            whenever(getNewsListUseCase.getNews(CountryCode, Category, DEFAULT_PAGE_INDEX))
                .thenReturn(DataState.Loading())

            //When
            viewModel.sendIntent(NewsIntent.FetchNews(CountryCode, Category, DEFAULT_PAGE_INDEX))

            //Then
            assertThat(viewModel.newsResponse.value).isNotNull()
            assertThat(viewModel.newsResponse.value.data).isNull()
            assertThat(viewModel.newsResponse.value.message).isNull()
        }
    }

    @Test
    fun `when calling for results then return news results`() {
        coroutineRule.runBlockingTest {

            // Mock repository response with a success state containing fake news articles
            val fakeArticles = FakeDataUtil.getFakeArticles().toList()
            val successResponse = DataState.Success(fakeArticles)
            whenever(getNewsListUseCase.getNews(CountryCode, Category, DEFAULT_PAGE_INDEX))
                .thenReturn(successResponse)

            // Call fetchNews
            viewModel.sendIntent(NewsIntent.FetchNews(CountryCode, Category, DEFAULT_PAGE_INDEX))

            // Assert on the ViewModel's state
            assertThat(viewModel.newsResponse.value).isInstanceOf(DataState::class.java)
            val response = viewModel.newsResponse.value
            assertThat(response).isInstanceOf(DataState.Success::class.java)
            assertThat(response.data).isNotNull()
            assertThat(response.data).hasSize(fakeArticles.size)
            assertThat(response.data).containsExactlyElementsIn(fakeArticles).inOrder()
        }
    }

    @Test
    fun `when calling for results then return error`() {
        coroutineRule.runBlockingTest {
            // Stub repository with fake favorites
            whenever(getNewsListUseCase.getNews(CountryCode, Category, DEFAULT_PAGE_INDEX))
                .thenAnswer { DataState.Error("Error occurred", null) }

            //When
            viewModel.sendIntent(NewsIntent.FetchNews(CountryCode, Category, DEFAULT_PAGE_INDEX))

            //then
            val response = viewModel.newsResponse.value
            assertThat(response.message).isNotNull()
            assertThat(response.message).isEqualTo("Error occurred")
        }
    }

    @After
    fun release() {
        Mockito.framework().clearInlineMocks()
    }
}