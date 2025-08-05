package com.ahmadrd.storyapp.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ahmadrd.storyapp.data.remote.response.story.ListStoryItem
import com.ahmadrd.storyapp.databinding.ItemRowStoryBinding
import com.ahmadrd.storyapp.ui.detail.DetailActivity
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    inner class ViewHolder(private val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListStoryItem) {
            with(binding) {
                Glide.with(root.context)
                    .load(item.photoUrl)
                    .into(storyPhoto)
                tvTitleStory.text = item.name
                tvDescStory.text = item.description

                root.setOnClickListener {
                    val intent = Intent(root.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.EXTRA_ID, item.id)

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(storyPhoto, "storyPhoto"),
                            Pair(tvTitleStory, "TitleStory"),
                            Pair(tvDescStory, "DescStory"),
                        )
                    root.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}