package com.ahmadrd.storyapp.utils

sealed class ErrorType {
    // Untuk error dari API dengan pesan dinamis
    data class ApiError(val message: String) : ErrorType()
    // Untuk error dengan pesan statis yang bisa diterjemahkan
    data class ResourceError(val resId: Int) : ErrorType()
}