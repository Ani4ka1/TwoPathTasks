package com.example.twopathtask

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var accDatabase: AccDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etRepeatPassword)
        btnRegister = findViewById(R.id.btnRegister)
        accDatabase = AccDatabase.getDatabase(this)

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (validateInput(username, password, confirmPassword)) {
                registerUser(username, password)
            }
        }
    }

    private fun validateInput(username: String, password: String, confirmPassword: String): Boolean {
        if (username.length < 4) {
            etUsername.error = "Username must be at least 4 characters"
            return false
        }

        if (password.length < 5) {
            etPassword.error = "Password must be at least 5 characters"
            return false
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            return false
        }

        return true
    }

    private fun registerUser(username: String, password: String) {
        lifecycleScope.launch {
            val account = Account(username = username, password = password)
            accDatabase.accountDao().insert(account)
            Toast.makeText(this@RegisterActivity, "User registered successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
