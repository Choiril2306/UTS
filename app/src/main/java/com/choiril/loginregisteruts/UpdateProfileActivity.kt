package com.choiril.loginregisteruts

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.widya.loginregisteruts.R
import kotlinx.coroutines.*

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private var currentUserId: Int = -1
    private var currentPassword: String = "" // ✅ Simpan password lama

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        val passwordEditText = findViewById<EditText>(R.id.editPassword)
        val togglePassword = findViewById<ImageView>(R.id.ivTogglePassword)

        // Initialize password visibility toggle
        togglePassword.setOnClickListener {
            // Toggle password visibility
            if (passwordEditText.inputType == (android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePassword.setImageResource(R.drawable.ic_visibility) // Change icon to "visible"
            } else {
                passwordEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePassword.setImageResource(R.drawable.ic_visibility_off) // Change icon to "hidden"
            }
            // Move cursor to end of password text
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        // Menambahkan tombol Cancel untuk kembali ke HomeActivity
        val cancelButton = findViewById<Button>(R.id.btnCancel)

        cancelButton.setOnClickListener {
            // Kembali ke HomeActivity tanpa menyimpan perubahan
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Tutup UpdateProfileActivity
        }


        // Initialize SessionManager and check for user email
        SessionManager.init(applicationContext)

        val email = SessionManager.getUserEmail()
        if (email == null) {
            finish() // Close activity if no user email found
            return
        }

        // Initialize database
        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app_db").build()

        val nameEditText = findViewById<EditText>(R.id.editName)
        val emailEditText = findViewById<EditText>(R.id.editEmail)
        val phoneEditText = findViewById<EditText>(R.id.editPhone)
        val addressEditText = findViewById<EditText>(R.id.editAddress)
        val updateButton = findViewById<Button>(R.id.btnUpdate)

        // Fetch user data from the database
        CoroutineScope(Dispatchers.IO).launch {
            val user = database.userDao().getUserByEmail(email)
            user?.let {
                currentUserId = it.id
                currentPassword = it.password // Store old password
                withContext(Dispatchers.Main) {
                    // Populate fields with current user data
                    nameEditText.setText(it.name)
                    emailEditText.setText(it.email)
                    phoneEditText.setText(it.phone)
                    addressEditText.setText(it.address)
                    passwordEditText.setText(it.password) // Display current password
                }
            }
        }

        // Handle update button click
        updateButton.setOnClickListener {
            val updatedPassword = passwordEditText.text.toString()
            val finalPassword = if (updatedPassword.isNotEmpty()) updatedPassword else currentPassword // Use new password or keep the old one

            val updatedUser = User(
                id = currentUserId,
                name = nameEditText.text.toString(),
                email = emailEditText.text.toString(),
                password = finalPassword, // Use new or old password
                phone = phoneEditText.text.toString(),
                address = addressEditText.text.toString()
            )

            // Update user in the database
            CoroutineScope(Dispatchers.IO).launch {
                database.userDao().update(updatedUser)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UpdateProfileActivity, "Profile Updated!", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity
                }
            }
        }
    }
}