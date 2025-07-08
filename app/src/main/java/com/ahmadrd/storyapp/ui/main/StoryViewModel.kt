package com.ahmadrd.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ahmadrd.storyapp.data.remote.response.story.ListStoryItem
import com.ahmadrd.storyapp.data.Repository
import com.ahmadrd.storyapp.utils.ResultState

class StoryViewModel(private val repository: Repository) : ViewModel() {

    val isLogin: LiveData<Boolean> = repository.isUserLoggedIn().asLiveData()

    private val _stories = MediatorLiveData<ResultState<List<ListStoryItem?>?>>()
    val stories: LiveData<ResultState<List<ListStoryItem?>?>> = _stories

    init {
        getStories()
    }

     private fun getStories() {
        val resultLiveData = repository.getStories()
        _stories.addSource(resultLiveData) { result ->
            _stories.value = result
            if (result is ResultState.Success || result is ResultState.Error) {
                _stories.removeSource(resultLiveData)
            }
        }
    }
}