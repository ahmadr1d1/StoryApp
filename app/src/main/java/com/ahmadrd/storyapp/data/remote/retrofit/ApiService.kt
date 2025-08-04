package com.ahmadrd.storyapp.data.remote.retrofit

import com.ahmadrd.storyapp.data.remote.response.auth.LoginResponse
import com.ahmadrd.storyapp.data.remote.response.auth.RegisterResponse
import com.ahmadrd.storyapp.data.remote.response.story.DetailStoryResponse
import com.ahmadrd.storyapp.data.remote.response.story.StoryResponse
import com.ahmadrd.storyapp.data.remote.response.story.UploadStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(): StoryResponse

    @GET("stories/{id}")
    suspend fun getStoryDetail(@Path("id") id: String): DetailStoryResponse

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): UploadStoryResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location : Int = 1,
    ): StoryResponse
}