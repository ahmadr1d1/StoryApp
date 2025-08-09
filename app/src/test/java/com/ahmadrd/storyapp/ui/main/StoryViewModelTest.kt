package com.ahmadrd.storyapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.ahmadrd.storyapp.DataDummy
import com.ahmadrd.storyapp.MainDispatcherRule
import com.ahmadrd.storyapp.data.Repository
import com.ahmadrd.storyapp.data.remote.response.story.ListStoryItem
import com.ahmadrd.storyapp.getOrAwaitValue
import com.ahmadrd.storyapp.ui.adapter.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: Repository

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStoryItems = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStoryItems)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStories)

        val storyViewModel = StoryViewModel(storyRepository)
        val actualStories: PagingData<ListStoryItem> = storyViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStoryItems.size, differ.snapshot().size)
        Assert.assertEquals(dummyStoryItems[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStories)
        val storyViewModel = StoryViewModel(storyRepository)
        val actualStories: PagingData<ListStoryItem> = storyViewModel.stories.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)
        Assert.assertEquals(0, differ.snapshot().size)
    }

    @Test
    fun `when user is logged in should return true`() {
        val expectedLoginStatus = true
        val liveData = MutableLiveData<Boolean>()
        liveData.value = expectedLoginStatus

        Mockito.`when`(storyRepository.isUserLoggedIn()).thenReturn(liveData)

        val storyViewModel = StoryViewModel(storyRepository)

        val actualLoginStatus = storyViewModel.isLogin.getOrAwaitValue()

        Assert.assertNotNull(actualLoginStatus)
        Assert.assertEquals(expectedLoginStatus, actualLoginStatus)
    }

    @Test
    fun `when user is not logged in should return false`() {
        val expectedLoginStatus = false
        val liveData = MutableLiveData<Boolean>()
        liveData.value = expectedLoginStatus

        Mockito.`when`(storyRepository.isUserLoggedIn()).thenReturn(liveData)

        val storyViewModel = StoryViewModel(storyRepository)

        val actualLoginStatus = storyViewModel.isLogin.getOrAwaitValue()

        Assert.assertNotNull(actualLoginStatus)
        Assert.assertEquals(expectedLoginStatus, actualLoginStatus)
    }

}

class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}