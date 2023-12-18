package com.example.firebase_auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val signupLink = findViewById<TextView>(R.id.tv_signup)
        val loginButton = findViewById<Button>(R.id.btn_login)

        signupLink.setOnClickListener {
            val intent = (Intent(this@MainActivity, RegisterActivity::class.java))
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = findViewById<EditText>(R.id.et_email).text.toString()
        val password = findViewById<EditText>(R.id.et_password).text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val registeredUser = checkUser(email, password)

                    if (registeredUser) {
                        // User is registered, proceed with the appropriate actions
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "Login successful", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "User not registered or invalid credentials", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            Toast.makeText(this@MainActivity, "Please enter email and password", Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun checkUser(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: FirebaseAuthException) {
            withContext(Dispatchers.Main) {
                when (e.errorCode) {
                    "ERROR_USER_NOT_FOUND" -> {
                        Toast.makeText(this@MainActivity, "No user found with this email", Toast.LENGTH_LONG).show()
                    }
                    "ERROR_WRONG_PASSWORD" -> {
                        Toast.makeText(this@MainActivity, "Incorrect password", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(this@MainActivity, "Authentication failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
            false
        }
    }



}