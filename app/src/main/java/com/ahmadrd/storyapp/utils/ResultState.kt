package com.ahmadrd.storyapp.utils

sealed class ResultState<out R> private constructor() {
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val error: ErrorType) : ResultState<Nothing>()
    data object Loading : ResultState<Nothing>()
}