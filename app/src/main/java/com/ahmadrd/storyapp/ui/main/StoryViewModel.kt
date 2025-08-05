package com.ahmadrd.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ahmadrd.storyapp.data.Repository
import com.ahmadrd.storyapp.data.remote.response.story.ListStoryItem

class StoryViewModel(repository: Repository) : ViewModel() {

    val isLogin: LiveData<Boolean> = repository.isUserLoggedIn().asLiveData()

    val stories: LiveData<PagingData<ListStoryItem>> =
        repository.getStories().cachedIn(viewModelScope)
}