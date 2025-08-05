package com.ahmadrd.storyapp.data.local.database

import androidx.paging.PagingSource
import androidx.room.*
import com.ahmadrd.storyapp.data.remote.response.story.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<ListStoryItem>)

    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}
