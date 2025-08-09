package com.ahmadrd.storyapp

import com.ahmadrd.storyapp.data.remote.response.story.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "$i",
                "$i",
                "$i",
                i.toDouble(),
                "$i",
                i.toDouble()
            )
            items.add(story)
        }
        return items
    }
}
