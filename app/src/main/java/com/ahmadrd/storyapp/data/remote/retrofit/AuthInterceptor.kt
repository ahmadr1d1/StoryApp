package com.ahmadrd.storyapp.data.remote.retrofit

import com.ahmadrd.storyapp.data.local.pref.UserPreference
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val userPreference: UserPreference
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val token = runBlocking {
            userPreference.getToken()
        }

        val newRequest = if (token.isNotEmpty() && request.header("No-Authentication") == null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        return chain.proceed(newRequest)
    }
}
