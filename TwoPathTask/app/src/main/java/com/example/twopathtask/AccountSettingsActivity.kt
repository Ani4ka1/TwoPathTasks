package com.example.twopathtask

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var etNewUsername: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var btnUpdateAccount: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        etNewUsername = findViewById(R.id.etNewUsername)
        etNewPassword = findViewById(R.id.etNewPassword)
        btnUpdateAccount = findViewById(R.id.btnUpdateAccount)
        btnLogout = findViewById(R.id.btnLogout)

        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val accountId = sharedPref.getInt("account_id", -1)

        btnUpdateAccount.setOnClickListener {
            val newUsername = etNewUsername.text.toString()
            val newPassword = etNewPassword.text.toString()

            if (newUsername.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = AccDatabase.getDatabase(this)
            val accountDao = db.accountDao()

            lifecycleScope.launch {
                val account = Account(accountId, newUsername, newPassword)
                accountDao.update(account)
                Toast.makeText(this@AccountSettingsActivity, "Account updated successfully", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogout.setOnClickListener {
            with(sharedPref.edit()) {
                remove("account_id")
                apply()
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
