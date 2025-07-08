package com.ahmadrd.storyapp.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.ahmadrd.storyapp.data.remote.response.auth.LoginResult
import com.ahmadrd.storyapp.data.Repository
import com.ahmadrd.storyapp.utils.ResultState

class LoginViewModel(private val repository: Repository) : ViewModel() {

    private val _loginResult = MediatorLiveData<ResultState<LoginResult>>()
    val loginResult: LiveData<ResultState<LoginResult>> = _loginResult

    fun performLogin(email: String, password: String) {
        val resultLiveData = repository.login(email, password) // return LiveData

        _loginResult.addSource(resultLiveData) { result ->
            _loginResult.value = result
            if (result is ResultState.Success || result is ResultState.Error) {
                _loginResult.removeSource(resultLiveData)
            }
        }
    }
}