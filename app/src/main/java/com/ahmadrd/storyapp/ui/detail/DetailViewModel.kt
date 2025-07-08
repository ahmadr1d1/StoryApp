package com.ahmadrd.storyapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.ahmadrd.storyapp.data.remote.response.story.Story
import com.ahmadrd.storyapp.data.Repository
import com.ahmadrd.storyapp.utils.ResultState

class DetailViewModel(private val repository: Repository) : ViewModel() {

    private val _detailStory = MediatorLiveData<ResultState<Story?>>()
    val detailStory: LiveData<ResultState<Story?>> = _detailStory

    fun getStoryDetail(id: String) {
        val resultLiveData = repository.getStoryDetail(id)
        _detailStory.addSource(resultLiveData) { result ->
            _detailStory.value = result
            if (result is ResultState.Success || result is ResultState.Error) {
                _detailStory.removeSource(resultLiveData)
            }
        }
    }
}