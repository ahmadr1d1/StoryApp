package com.ahmadrd.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.ahmadrd.storyapp.data.Repository
import com.ahmadrd.storyapp.data.remote.response.story.ListStoryItem
import com.ahmadrd.storyapp.utils.ResultState

class MapsViewModel(private val repository: Repository) : ViewModel() {
    private val _stories = MediatorLiveData<ResultState<List<ListStoryItem?>?>>()
    val stories: LiveData<ResultState<List<ListStoryItem?>?>> = _stories

    init {
        getStoriesWithLocation()
    }

    private fun getStoriesWithLocation() {
        val resultLiveData = repository.getStoriesWithLocation()
        _stories.addSource(resultLiveData) { result ->
            _stories.value = result
            if (result is ResultState.Success || result is ResultState.Error) {
                _stories.removeSource(resultLiveData)
            }
        }
    }
}