package com.example.firebase_auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val btnRegister = findViewById<Button>(R.id.btn_signup)

        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    fun registerUser() {
        val email = findViewById<EditText>(R.id.et_email).text.toString()
        val password = findViewById<EditText>(R.id.et_password).text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Create the user
                    auth.createUserWithEmailAndPassword(email, password).await()

                    // Check if the user already exists
                    val existingUser = checkUserExists(email)

                    withContext(Dispatchers.Main) {
                        if (existingUser) {
                            // User already exists
                            Toast.makeText(
                                this@RegisterActivity,
                                "User with this email already exists. Please login",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            // Navigate to login screen
                            val returnToLogin = Intent(this@RegisterActivity, MainActivity::class.java)
                            startActivity(returnToLogin)

                            // Show success message
                            Toast.makeText(
                                this@RegisterActivity,
                                "You registered successfully",
                                Toast.LENGTH_LONG
                            ).show()

                            // Finish the current activity
                            finish()
                        }
                    }
                } catch (e: Exception) {
                    // Handle exceptions
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


    private suspend fun checkUserExists(email: String): Boolean {
        val querySnapshot = firestore.collection("users").whereEqualTo("email", email).get().await()
        return !querySnapshot.isEmpty
    }

}