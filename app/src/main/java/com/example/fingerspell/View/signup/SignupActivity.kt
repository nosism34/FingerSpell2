package com.example.fingerspell.ui.signup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.example.fingerspell.View.costumView.Button
import com.example.fingerspell.View.costumView.TextEdit
import com.example.fingerspell.data.API.ApiConfig.getApiService
import com.example.fingerspell.data.API.ApiService
import com.example.fingerspell.data.LoginResponse
import com.example.fingerspell.databinding.ActivitySignupBinding
import com.example.fingerspell.ui.login.LoginActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var button: Button
    private lateinit var passtextedit: TextEdit
    private lateinit var emailText: TextInputEditText
    private lateinit var rePass: TextEdit
    private lateinit var mContext: Context
    private lateinit var mApiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupView()
        setupAction()

        // Inisialisasi propearti
        button = binding.signupButton
        passtextedit = binding.password
        emailText = binding.emailEditText
        rePass = binding.rePassword

        // Set TextWatchers
        setTextWatchers()
        setMyButtonEnable()
    }

    private fun setTextWatchers() {
        passtextedit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })

        emailText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })

        rePass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun requestRegister() {
        showLoading(true)

        lifecycleScope.launch {
            val email: String = binding.emailEditText.text.toString()
            val password: String = binding.password.text.toString()
            val rePass: String = binding.rePassword.text.toString()
            try {
                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                intent.putExtra("email", email)
                val apiService = getApiService("")
                apiService.register(email, password, rePass)
                showToast("User Account Created")
                showLoading(false)
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@SignupActivity as Activity).toBundle())
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                errorResponse.message?.let { showToast(it) }
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setMyButtonEnable() {
        val password = passtextedit.text
        val email = emailText.text
        val rePassword = rePass.text
        button.isEnabled = password != null && password.toString().isNotEmpty()
                && email != null && email.toString().isNotEmpty()
                && password.toString().length >= 8
                && rePassword != null && rePassword.toString().isNotEmpty()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            requestRegister()
        }
    }
}
