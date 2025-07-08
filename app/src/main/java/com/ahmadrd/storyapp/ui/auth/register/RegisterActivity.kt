package com.ahmadrd.storyapp.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
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
import com.ahmadrd.storyapp.databinding.ActivityRegisterBinding
import com.ahmadrd.storyapp.di.ViewModelFactory
import com.ahmadrd.storyapp.ui.auth.login.LoginActivity
import com.ahmadrd.storyapp.utils.ErrorType
import com.ahmadrd.storyapp.utils.ResultState

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var nameValueForDialog: String
    private val emailPattern = "^[a-zA-Z0-9._%+-]+@gmail\\.com$".toRegex(RegexOption.IGNORE_CASE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupAction()
        observeViewModel()
        playAnimation()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val nameValue = binding.nameEditText.text.toString().trim()
            val emailValue = binding.emailEditText.text.toString().trim()
            val passwordValue = binding.passwordEditText.text.toString().trim()

            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

            if (isInputValid(nameValue, emailValue, passwordValue)) {
                nameValueForDialog = nameValue
                viewModel.register(nameValue, emailValue, passwordValue)
            }
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun isInputValid(
        nameValue: String,
        emailValue: String,
        passwordValue: String
    ): Boolean {
        var isValid = true
        // Validasi Nama
        if (nameValue.isBlank()) {
            binding.nameEditTextLayout.error = getString(R.string.name_blank)
            isValid = false
        } else {
            binding.nameEditTextLayout.error = null
        }

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
        viewModel.register.observe(this) { register ->
            if (register != null) {
                when (register) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showLoading(false)
                        AlertDialog.Builder(this).apply {
                            setTitle(R.string.success_title)
                            setMessage(getString(R.string.register_success_message, nameValueForDialog))
                            setPositiveButton(R.string.next) { _, _ ->
                                val intent = Intent(context, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }

                    is ResultState.Error -> {
                        showLoading(false)
                        when (val errorType = register.error) {
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
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val register =
            ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)
        val title =
            ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val name =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val email =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val password =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val nameEdit =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailEdit =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordEdit =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val tvAskLogin =
            ObjectAnimator.ofFloat(binding.tvAskLogin, View.ALPHA, 1f).setDuration(100)
        val tvLogin =
            ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                name,
                nameEdit,
                email,
                emailEdit,
                password,
                passwordEdit,
                register,
                tvAskLogin,
                tvLogin
            )
            start()

        }
    }
}