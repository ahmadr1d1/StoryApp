package com.ahmadrd.storyapp.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.ahmadrd.storyapp.data.remote.response.auth.RegisterResponse
import com.ahmadrd.storyapp.data.Repository
import com.ahmadrd.storyapp.utils.ResultState

class RegisterViewModel(private val storyRepository: Repository) : ViewModel() {
    private val _register = MediatorLiveData<ResultState<RegisterResponse>>()
    val register: LiveData<ResultState<RegisterResponse>> = _register

    fun register(name: String, email: String, password: String) {
        val resultLiveData = storyRepository.register(name, email, password)
        _register.addSource(resultLiveData) { result ->
            _register.value = result
            if (result is ResultState.Success || result is ResultState.Error) {
                _register.removeSource(resultLiveData)
            }
        }
    }
}