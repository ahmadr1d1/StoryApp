package com.ahmadrd.storyapp.ui.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ahmadrd.storyapp.R
import com.ahmadrd.storyapp.databinding.ActivityLoginBinding
import com.ahmadrd.storyapp.di.ViewModelFactory
import com.ahmadrd.storyapp.ui.auth.register.RegisterActivity
import com.ahmadrd.storyapp.ui.main.StoryActivity
import com.ahmadrd.storyapp.utils.ErrorType
import com.ahmadrd.storyapp.utils.ResultState

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val emailPattern = "^[a-zA-Z0-9._%+-]+@gmail\\.com$".toRegex(RegexOption.IGNORE_CASE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observeViewModel()
        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val emailValue = binding.emailEditText.text.toString().trim()
            val passwordValue = binding.passwordEditText.text.toString().trim()


            val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

            if (isInputValid(emailValue, passwordValue)) {
                viewModel.performLogin(emailValue, passwordValue)
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun isInputValid(emailValue: String, passwordValue: String): Boolean {
        var isValid = true

        // Validasi Email
        if (emailValue.isBlank()) {
            binding.emailEditTextLayout.error = getString(R.string.email_blank)
            isValid = false
        } else if (!emailValue.matches(emailPattern)) {
            binding.emailEditTextLayout.error = getString(R.string.email_valid)
            isValid = false
        } else {
            binding.emailEditTextLayout.error = null
        }

        // Validasi Password
        if (passwordValue.isBlank()) {
            binding.passwordEditTextLayout.error = getString(R.string.password_blank)
            isValid = false
        } else if (passwordValue.length < 8) {
            binding.passwordEditTextLayout.error = getString(R.string.password_error_length)
            isValid = false
        } else {
            binding.passwordEditTextLayout.error = null
        }

        if (!isValid) {
            Toast.makeText(this, R.string.check_input_again, Toast.LENGTH_LONG).show()
        }
        return isValid
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    // Login berhasil, data sesi sudah disimpan oleh Repository
                    AlertDialog.Builder(this).apply {
                        setTitle(R.string.success_title)
                        setMessage(getString(R.string.login_success_message, result.data.name))
                        setCancelable(false)
                        setPositiveButton(R.string.next) { _, _ ->
                            val intent = Intent(context, StoryActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
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

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title =
            ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val email =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val password =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val emailEdit =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordEdit =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login =
            ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val tvAskRegister =
            ObjectAnimator.ofFloat(binding.tvAskRegister, View.ALPHA, 1f).setDuration(100)
        val tvRegister =
            ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                email,
                emailEdit,
                password,
                passwordEdit,
                login,
                tvAskRegister,
                tvRegister
            )
            start()

        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}