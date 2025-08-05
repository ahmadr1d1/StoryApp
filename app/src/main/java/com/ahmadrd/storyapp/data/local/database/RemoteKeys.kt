package com.ahmadrd.storyapp.data.local.database

import androidx.room.*

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)