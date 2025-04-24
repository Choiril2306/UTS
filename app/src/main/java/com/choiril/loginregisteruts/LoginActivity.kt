package com.choiril.loginregisteruts

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.widya.loginregisteruts.R
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        SessionManager.init(applicationContext)

        if (SessionManager.getLoginState()) {
            goToHomeActivity()
        }

        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app_db").build()

        val loginButton = findViewById<Button>(R.id.btnLogin)
        val emailEditText = findViewById<EditText>(R.id.editEmail)
        val passwordEditText = findViewById<EditText>(R.id.editPassword)
        val registerTextView = findViewById<TextView>(R.id.tvRegister)
        val togglePassword = findViewById<ImageView>(R.id.ivTogglePassword)

        var isPasswordVisible = false
        togglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePassword.setImageResource(R.drawable.ic_visibility)
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePassword.setImageResource(R.drawable.ic_visibility_off)
            }
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                val user = database.userDao().getUserByEmailAndPassword(email, password)
                withContext(Dispatchers.Main) {
                    if (user != null) {
                        SessionManager.setLoginState(true, email)
                        goToHomeActivity()
                    } else {
                        Toast.makeText(this@LoginActivity, "Email atau password salah", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}