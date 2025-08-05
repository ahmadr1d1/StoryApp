package com.ahmadrd.storyapp.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.ahmadrd.storyapp.data.remote.response.story.UploadStoryResponse
import com.ahmadrd.storyapp.data.Repository
import com.ahmadrd.storyapp.utils.ResultState
import java.io.File

class AddStoryViewModel(private val repository: Repository) : ViewModel() {

    private val _uploadStory = MediatorLiveData<ResultState<UploadStoryResponse>>()
    val uploadStory: LiveData<ResultState<UploadStoryResponse>> = _uploadStory

    fun uploadImage(
        imageFile: File,
        description: String,
        lat: Double?,
        lon: Double?
    ) {
        val resultLiveData = repository.uploadImage(imageFile, description, lat, lon)
        _uploadStory.addSource(resultLiveData) { result ->
            _uploadStory.value = result
            if (result is ResultState.Success || result is ResultState.Error) {
                _uploadStory.removeSource(resultLiveData)
            }
        }
    }
}