package com.ahmadrd.storyapp.di

import android.content.Context
import com.ahmadrd.storyapp.data.Repository
import com.ahmadrd.storyapp.data.local.database.StoryDatabase
import com.ahmadrd.storyapp.data.local.pref.UserPreference
import com.ahmadrd.storyapp.data.local.pref.dataStore
import com.ahmadrd.storyapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): Repository {
        val storyDatabase = StoryDatabase.getDatabase(context)
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService("")
        return Repository.getInstance(storyDatabase, pref, apiService)
    }
}