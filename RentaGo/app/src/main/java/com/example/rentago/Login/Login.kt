package com.example.rentago.Login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rentago.MainActivity
import com.example.rentago.Requests.LoginRequest
import com.example.rentago.Response.LoginResponse
import com.example.rentago.Retrofit.Registration.Login.RetrofitLoginClient
import com.example.rentago.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initializing ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        binding.loginButton.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val username = binding.loginUsername.text.toString().trim().ifEmpty { null }
        val email = binding.loginEmail.text.toString().trim().ifEmpty { null }
        val password = binding.loginPassword.text.toString().trim()

        if ((username == null && email == null) || password.isEmpty()) {
            Toast.makeText(this, "Username/Email and Password required", Toast.LENGTH_SHORT).show()
            return
        }

        val loginRequest = LoginRequest(username, email, password)

        RetrofitLoginClient.instance.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        tokenManager.saveTokens(it.accessToken, it.refreshToken)

                        // Extract user details
                        val user = it.user
                        Toast.makeText(this@Login, "Welcome ${user.username}!", Toast.LENGTH_SHORT).show()

                        navigateToMainActivity()
                    }
                } else {
                    Toast.makeText(this@Login, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@Login, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
