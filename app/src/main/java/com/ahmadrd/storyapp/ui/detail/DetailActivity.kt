package com.ahmadrd.storyapp.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.ahmadrd.storyapp.R
import com.ahmadrd.storyapp.databinding.ActivityDetailBinding
import com.ahmadrd.storyapp.di.ViewModelFactory
import com.ahmadrd.storyapp.utils.ErrorType
import com.ahmadrd.storyapp.utils.ResultState

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val storyId = intent.getStringExtra(EXTRA_ID)
        if (storyId != null) {
            viewModel.getStoryDetail(storyId)
            observeViewModel()
        } else {
            Toast.makeText(this, R.string.invalid_id, Toast.LENGTH_LONG).show()
        }

        binding.toolbarHome.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeViewModel() {
        viewModel.detailStory.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    val story = result.data
                    if (story != null) {
                        with(binding) {
                            tvTitleStory.text = story.name
                            tvDescStory.text = story.description
                        }
                        Glide.with(this)
                            .load(story.photoUrl)
                            .error(R.drawable.logo_story)
                            .into(binding.storyPhoto)
                    }
                }

                is ResultState.Error -> {
                    showLoading(false)
                    when (val errorType = result.error) {
                        is ErrorType.ApiError -> {
                            // Jika error dari API, tampilkan pesannya langsung
                            Toast.makeText(
                                this, errorType.message, Toast.LENGTH_LONG
                            ).show()
                        }

                        is ErrorType.ResourceError -> {
                            // Jika error dari resource, gunakan getString untuk menerjemahkannya
                            Toast.makeText(
                                this, getString(errorType.resId), Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}