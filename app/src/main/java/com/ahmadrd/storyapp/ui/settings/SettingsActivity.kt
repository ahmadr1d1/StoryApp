package com.ahmadrd.storyapp.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ahmadrd.storyapp.R
import com.ahmadrd.storyapp.databinding.ActivitySettingsBinding
import com.ahmadrd.storyapp.di.ViewModelFactory
import com.ahmadrd.storyapp.ui.auth.login.LoginActivity
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel by viewModels<SettingsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        observeViewModel()
        setupAction()
    }

    private fun observeViewModel() {
        viewModel.darkMode.observe(this) { isDark ->
            binding.switchTheme.isChecked = isDark
            val mode =
                if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        viewModel.language.observe(this) { code ->
            val language = when (code) {
                "in" -> "Indonesia"
                "en" -> "English"
                "ms" -> "Malaysia(Melayu)"
                "fr" -> "French"
                else -> code
            }
            binding.tvCurrentLanguage.text = language
        }
    }

    private fun setupAction() {
        with(binding) {
            toolbarHome.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            switchTheme.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setDarkMode(isChecked)
            }

            settingLanguage.setOnClickListener {
                showLanguageDialog()
            }

            buttonLogout.setOnClickListener {
                viewModel.logout()
                val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("English", "Indonesia", "Malaysia(Melayu)", "French")
        val codes = arrayOf("en", "in", "ms", "fr")

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.choose_language)
        builder.setSingleChoiceItems(languages, -1) { dialog, which ->
            val selectedCode = codes[which]
            viewModel.setLanguage(selectedCode)

            // Terapkan bahasa
            updateResources(selectedCode)
            recreate()

            dialog.dismiss()
        }
        builder.create().show()
    }

    @SuppressLint("AppBundleLocaleChanges")
    private fun updateResources(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}