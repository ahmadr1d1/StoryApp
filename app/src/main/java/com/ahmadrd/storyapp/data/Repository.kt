package com.ahmadrd.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.ahmadrd.storyapp.R
import com.ahmadrd.storyapp.data.local.pref.UserPreference
import com.ahmadrd.storyapp.data.remote.response.auth.*
import com.ahmadrd.storyapp.data.remote.response.story.*
import com.ahmadrd.storyapp.data.remote.retrofit.ApiService
import com.ahmadrd.storyapp.utils.ErrorType
import com.ahmadrd.storyapp.utils.ResultState
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class Repository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    fun isUserLoggedIn(): Flow<Boolean> = userPreference.isUserLoggedIn()

    suspend fun logout() = userPreference.logout()

    fun getDarkMode() = userPreference.getDarkMode()
    suspend fun setDarkMode(enabled: Boolean) = userPreference.setDarkMode(enabled)

    fun getLanguage() = userPreference.getLanguage()
    suspend fun setLanguage(code: String) = userPreference.setLanguage(code)

    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<ResultState<RegisterResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.register(name, email, password)
            if (!response.error) {
                emit(ResultState.Success(response))
            } else {
                emit(ResultState.Error(ErrorType.ApiError(response.message)))
            }
        } catch (e: HttpException) {
            if (e.code() == 400) {
                emit(ResultState.Error(ErrorType.ResourceError(R.string.already_taken)))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                emit(ResultState.Error(ErrorType.ApiError(errorResponse.message)))
            }
        } catch (e: Exception) {
            Log.e("SignUpViewModel", "postSignUp: ${e.message.toString()}")
            emit(ResultState.Error(ErrorType.ApiError(e.message.toString())))
        }
    }

    fun login(
        email: String,
        password: String
    ): LiveData<ResultState<LoginResult>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.login(email, password)
            if (!response.error) {
                val loginResult = response.loginResult
                userPreference.saveSession(loginResult) // Menyimpan data login ke sesi
                emit(ResultState.Success(loginResult))
            } else {
                emit(ResultState.Error(ErrorType.ApiError(response.message)))
            }
        } catch (e: HttpException) {
            if (e.code() == 401) {
                emit(ResultState.Error(ErrorType.ResourceError(R.string.error_wrong_credentials)))
            } else {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                emit(ResultState.Error(ErrorType.ApiError(errorResponse.message)))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(ErrorType.ResourceError(R.string.login_failed)))
        }
    }

    fun getStories(): LiveData<ResultState<List<ListStoryItem>>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.getStories()
            if (!response.error) {
                emit(ResultState.Success(response.listStory))
            } else {
                emit(ResultState.Error(ErrorType.ApiError(response.message)))
            }
        } catch (e: Exception) {
            Log.e("StoryViewModel", "getStories: ${e.message.toString()}")
            emit(ResultState.Error(ErrorType.ApiError(e.message.toString())))
        }
    }

    fun getStoryDetail(id: String): LiveData<ResultState<Story?>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.getStoryDetail(id)
            if (!response.error) {
                emit(ResultState.Success(response.story))
            } else {
                emit(ResultState.Error(ErrorType.ApiError(response.message)))
            }
        } catch (e: Exception) {
            Log.e("StoryViewModel", "getStoryDetail: ${e.message.toString()}")
            emit(ResultState.Error(ErrorType.ApiError(e.message.toString())))
        }
    }

    fun uploadImage(imageFile: File, description: String) = liveData {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.uploadImage(multipartBody, requestBody)
            when {
                successResponse.error -> emit(ResultState.Error(
                    ErrorType.ApiError(successResponse.message)))
                else -> emit(ResultState.Success(successResponse))
            }
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadStoryResponse::class.java)
            emit(ResultState.Error(ErrorType.ApiError(errorResponse.message)))
        }

    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(userPreference, apiService)
            }.also { instance = it }
    }
}