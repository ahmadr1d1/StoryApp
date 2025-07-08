package com.ahmadrd.storyapp.utils

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import com.ahmadrd.storyapp.R
import com.google.android.material.textfield.TextInputLayout

class MyEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private val emailPattern = "^[a-zA-Z0-9._%+-]+@gmail\\.com$".toRegex(RegexOption.IGNORE_CASE)

    // Enum untuk tipe validasi agar kode lebih mudah dibaca
    private enum class ValidationType {
        NONE, EMAIL, PASSWORD
    }

    init {
        // Ambil atribut kustom dari XML
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MyEditText,
            0, 0
        ).apply {
            try {
                val validationType =
                    getInt(R.styleable.MyEditText_validationType, ValidationType.NONE.ordinal)

                // Terapkan TextWatcher berdasarkan tipe validasi
                when (validationType) {
                    ValidationType.EMAIL.ordinal -> setupEmailValidation()
                    ValidationType.PASSWORD.ordinal -> setupPasswordValidation()
                    else -> { // Nothing
                    }
                }
            } finally {
                recycle()
            }
        }
    }

    private fun setupEmailValidation() {
        addTextChangedListener { text ->
            val email = text.toString()
            val textInputLayout = parent.parent as? TextInputLayout

            if (!email.matches(emailPattern)) {
                textInputLayout?.error = context.getString(R.string.email_valid)
            } else {
                this.error = null
                textInputLayout?.error = null
            }
        }
    }

    private fun setupPasswordValidation() {
        addTextChangedListener { text ->
            val password = text.toString()
            val textInputLayout = parent.parent as? TextInputLayout

            if (password.length < 8) {
                textInputLayout?.error = context.getString(R.string.password_error_length)
            } else {
                this.error = null
                textInputLayout?.error = null
            }
        }
    }
}