package com.example.twopathtask

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var accDatabase: AccDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        accDatabase = AccDatabase.getDatabase(this)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (validateInput(username, password)) {
                loginUser(username, password)
            }
        }
    }

    private fun validateInput(username: String, password: String): Boolean {
        if (username.isBlank()) {
            etUsername.error = "Username is required"
            return false
        }

        if (password.isBlank()) {
            etPassword.error = "Password is required"
            return false
        }

        return true
    }

    private fun loginUser(username: String, password: String) {
        lifecycleScope.launch {
            val account = accDatabase.accountDao().getAccount(username, password)
            if (account != null) {
                // Save account ID to SharedPreferences
                val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putInt("account_id", account.id)
                    apply()
                }

                Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "No such user found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
