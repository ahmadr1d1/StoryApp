package com.ahmadrd.storyapp.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadrd.storyapp.R
import com.ahmadrd.storyapp.data.local.pref.UserPreference
import com.ahmadrd.storyapp.data.local.pref.dataStore
import com.ahmadrd.storyapp.databinding.ActivityStoryBinding
import com.ahmadrd.storyapp.di.ViewModelFactory
import com.ahmadrd.storyapp.ui.adapter.StoryAdapter
import com.ahmadrd.storyapp.ui.add.AddStoryActivity
import com.ahmadrd.storyapp.ui.auth.login.LoginActivity
import com.ahmadrd.storyapp.ui.settings.SettingsActivity
import com.ahmadrd.storyapp.utils.ErrorType
import com.ahmadrd.storyapp.utils.ResultState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private val viewModel by viewModels<StoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var adapter: StoryAdapter
    private var isUserLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.storyActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        splashScreen.setKeepOnScreenCondition { !isUserLoggedIn } // Menghindari kilasan layar StoryActivity
        saveSettings()
        viewModel.isLogin.observe(this) { isLoggedInValue ->
            isUserLoggedIn = isLoggedInValue // Update flag
            if (isLoggedInValue) {
                setupMenu()
                setupRecyclerView()
                setupAction()
                observeViewModel()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun saveSettings() {
        val userPref = UserPreference.getInstance(dataStore)

        CoroutineScope(Dispatchers.Default).launch {
            val isDark = userPref.getDarkMode().firstOrNull() ?: false
            val lang = userPref.getLanguage().firstOrNull() ?: Locale.getDefault().language

            withContext(Dispatchers.Main) {
                val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                AppCompatDelegate.setDefaultNightMode(mode)

                setAppLocale(lang)
            }
        }
    }

    @SuppressLint("AppBundleLocaleChanges")
    private fun setAppLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setupAction() {
        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = StoryAdapter()
        binding.rvStory.apply {
            adapter = this@StoryActivity.adapter
            layoutManager = LinearLayoutManager(this@StoryActivity)
        }
    }

    private fun observeViewModel() {
        viewModel.stories.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    adapter.submitList(result.data)
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

    private fun setupMenu() {
        binding.toolbarHome.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
            }
            true
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}