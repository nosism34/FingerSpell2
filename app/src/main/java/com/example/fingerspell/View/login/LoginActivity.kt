package com.example.fingerspell.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.fingerspell.R
import com.example.fingerspell.View.HomeActivity
import com.example.fingerspell.View.costumView.Button
import com.example.fingerspell.View.costumView.TextEdit
import com.example.fingerspell.View.login.LoginViewModel
import com.example.fingerspell.data.API.ApiConfig
import com.example.fingerspell.data.LoginResponse
import com.example.fingerspell.data.pref.UserModel
import com.example.fingerspell.databinding.ActivityLoginBinding
import com.example.fingerspell.model.ViewModelFactory
import com.example.fingerspell.ui.signup.SignupActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private lateinit var accButton: Button
    private lateinit var Passtext: TextEdit
    private lateinit var EmailText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        accButton = binding.loginButton
        Passtext = binding.passwordEditText
        EmailText = binding.emailEditText
        val inputEmail: String? = intent.getStringExtra("email")

        EmailText.setText(inputEmail)
        binding.tvRegister.setOnClickListener{
            val intent = Intent(this@LoginActivity,SignupActivity::class.java)
            startActivity(intent)
        }
        setMyButtonEnable()
        setupView()
        setupAction()

        Passtext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        EmailText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

    }

    private fun setMyButtonEnable() {

        val password = Passtext.text
        val email = EmailText.text

        accButton.isEnabled = password != null && password.toString().isNotEmpty()
                && email != null && email.toString().isNotEmpty()
                && password.toString().length >= 8

    }

    private fun requestLogin() {
        showLoading(true)

        lifecycleScope.launch {
            val email: String = binding.emailEditText.text.toString()
            val password: String = binding.passwordEditText.text.toString()
            try {
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                val apiService = ApiConfig.getApiService("")
                val successResponse = apiService.login(email, password)

                showToast("Login Success")
                showLoading(false)

                try {
                    val responseBody = successResponse
                    if (responseBody.message == "success") {
                        val token = responseBody.data.uid
                        if (token != null) {
                            Log.d("Token", token)
                        }
                        viewModel.saveSession(UserModel(email, token))

                    } else {
                        Log.e("Login", "Login failed")
                    }

                } catch (e: Exception) {
                    Log.e("JSON", "Error parsing JSON: ${e.message}")
                }
                startActivity(intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity as Activity).toBundle())

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                showToast(errorResponse.message)
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

        binding.loginButton.setOnClickListener {
            requestLogin()
        }
    }

}

