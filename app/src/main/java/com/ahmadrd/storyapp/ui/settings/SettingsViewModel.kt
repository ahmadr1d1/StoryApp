package com.ahmadrd.storyapp.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ahmadrd.storyapp.data.Repository
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: Repository) : ViewModel() {

    val darkMode: LiveData<Boolean> = repository.getDarkMode().asLiveData()

    fun setDarkMode(enabled: Boolean) = viewModelScope.launch {
        repository.setDarkMode(enabled)
    }

    val language: LiveData<String> = repository.getLanguage().asLiveData()

    fun setLanguage(code: String) {
        viewModelScope.launch {
            repository.setLanguage(code)
        }
    }

    fun logout() = viewModelScope.launch {
        repository.logout()
    }
}